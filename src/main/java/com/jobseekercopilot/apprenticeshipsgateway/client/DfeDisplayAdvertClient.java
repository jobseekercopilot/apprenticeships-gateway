package com.jobseekercopilot.apprenticeshipsgateway.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.jobseekercopilot.apprenticeshipsgateway.config.ApprenticeshipsProperties;
import com.jobseekercopilot.apprenticeshipsgateway.model.ApprenticeshipAddress;
import com.jobseekercopilot.apprenticeshipsgateway.model.ApprenticeshipVacancy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class DfeDisplayAdvertClient {
    private static final Logger log = LoggerFactory.getLogger(DfeDisplayAdvertClient.class);

    private final ApprenticeshipsProperties properties;
    private final WebClient webClient;

    public DfeDisplayAdvertClient(ApprenticeshipsProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .codecs(codecs -> codecs.defaultCodecs()
                        .maxInMemorySize(properties.getMaxInMemoryResponseBytes()))
                .build();
    }

    public DfeVacancyPage fetchPage(int page) {
        try {
            JsonNode body = webClient.get().uri(builder -> builder.path("/vacancy")
                            .queryParam("PageNumber", page).queryParam("PageSize", properties.getPageSize())
                            .queryParam("IncludeDetails", true).queryParam("Sort", "AgeDesc").build())
                    .header("X-Version", Integer.toString(properties.getApiVersion()))
                    .header("Ocp-Apim-Subscription-Key", properties.getApiKey()).retrieve().bodyToMono(JsonNode.class).block();
            if (body == null) return new DfeVacancyPage(List.of(), 0, 0, 0);
            List<ApprenticeshipVacancy> vacancies = new ArrayList<>();
            body.path("vacancies").forEach(node -> vacancies.add(map(node)));
            return new DfeVacancyPage(List.copyOf(vacancies), integer(body, "total"), integer(body, "totalFiltered"), integer(body, "totalPages"));
        } catch (WebClientResponseException.TooManyRequests exception) {
            logFailure("RATE_LIMIT", exception.getStatusCode().value());
            throw new DfeProviderException("DfE Display Advert API rate limit reached", HttpStatus.TOO_MANY_REQUESTS);
        } catch (WebClientResponseException exception) {
            HttpStatus status = exception.getStatusCode() == HttpStatus.UNAUTHORIZED || exception.getStatusCode() == HttpStatus.FORBIDDEN
                    ? HttpStatus.valueOf(exception.getStatusCode().value()) : HttpStatus.SERVICE_UNAVAILABLE;
            logFailure(status == HttpStatus.SERVICE_UNAVAILABLE ? "HTTP_RESPONSE" : "AUTHENTICATION",
                    exception.getStatusCode().value());
            throw new DfeProviderException("DfE Display Advert API request failed", status);
        } catch (DfeProviderException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            logFailure(containsCause(exception, DataBufferLimitException.class)
                    ? "RESPONSE_CODEC_LIMIT" : "TRANSPORT_OR_DECODE", null);
            throw new DfeProviderException("DfE Display Advert API request failed", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private void logFailure(String category, Integer status) {
        log.warn("DfE Display Advert API request failed category={} status={} maxInMemoryResponseBytes={}",
                category, status == null ? "unavailable" : status, properties.getMaxInMemoryResponseBytes());
    }

    private boolean containsCause(Throwable throwable, Class<? extends Throwable> type) {
        Throwable current = throwable;
        while (current != null) {
            if (type.isInstance(current)) return true;
            current = current.getCause();
        }
        return false;
    }

    private ApprenticeshipVacancy map(JsonNode node) {
        List<ApprenticeshipAddress> addresses = new ArrayList<>();
        node.path("addresses").forEach(address -> addresses.add(new ApprenticeshipAddress(text(address,"addressLine1"), text(address,"addressLine2"),
                text(address,"addressLine3"), text(address,"addressLine4"), text(address,"postcode"), decimal(address,"latitude"), decimal(address,"longitude"))));
        List<String> skills = strings(node.path("skills"));
        List<String> qualifications = new ArrayList<>();
        node.path("qualifications").forEach(value -> qualifications.add(java.util.stream.Stream.of(text(value,"qualificationType"), text(value,"subject"), text(value,"grade"), text(value,"weighting"))
                .filter(item -> item != null && !item.isBlank()).collect(java.util.stream.Collectors.joining(" — "))));
        JsonNode wage = node.path("wage"); JsonNode course = node.path("course");
        return new ApprenticeshipVacancy(text(node,"vacancyReference"), text(node,"title"), text(node,"description"), text(node,"fullDescription"),
                text(node,"employerName"), text(node,"providerName"), text(node,"postedDate"), text(node,"closingDate"), text(node,"startDate"),
                decimal(wage,"wageAmount"), text(wage,"wageType"), text(wage,"wageUnit"), text(wage,"wageAdditionalInformation"), text(wage,"workingWeekDescription"),
                decimal(node,"hoursPerWeek"), text(node,"expectedDuration"), nullableInteger(node,"numberOfPositions"), List.copyOf(addresses),
                text(node,"applicationUrl"), text(node,"vacancyUrl"), text(course,"title"), nullableInteger(course,"level"), nullableInteger(course,"larsCode"),
                text(course,"route"), text(course,"type"), text(node,"apprenticeshipLevel"), bool(node,"isNationalVacancy"), text(node,"isNationalVacancyDetails"),
                skills, List.copyOf(qualifications), text(node,"thingsToConsider"), text(node,"companyBenefitsInformation"));
    }
    private String text(JsonNode node, String field) { return node.path(field).isMissingNode() || node.path(field).isNull() ? null : node.path(field).asText(); }
    private BigDecimal decimal(JsonNode node, String field) { return node.path(field).isNumber() ? node.path(field).decimalValue() : null; }
    private Integer nullableInteger(JsonNode node, String field) { return node.path(field).isIntegralNumber() ? node.path(field).asInt() : null; }
    private int integer(JsonNode node, String field) { return nullableInteger(node, field) == null ? 0 : nullableInteger(node, field); }
    private Boolean bool(JsonNode node, String field) { return node.path(field).isBoolean() ? node.path(field).asBoolean() : null; }
    private List<String> strings(JsonNode node) { List<String> values = new ArrayList<>(); node.forEach(value -> { if (value.isTextual()) values.add(value.asText()); }); return List.copyOf(values); }
}
