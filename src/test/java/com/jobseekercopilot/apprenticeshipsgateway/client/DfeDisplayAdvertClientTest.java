package com.jobseekercopilot.apprenticeshipsgateway.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.jobseekercopilot.apprenticeshipsgateway.config.ApprenticeshipsProperties;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class DfeDisplayAdvertClientTest {
    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) server.stop(0);
    }

    @Test
    void decodesLargeV2PageAndSendsDisplayAdvertRequestContract() throws IOException {
        AtomicReference<HttpExchange> request = new AtomicReference<>();
        String fullDescription = "provider vacancy detail ".repeat(13_000);
        byte[] response = ("""
                {
                  "vacancies": [{
                    "vacancyReference": "VAC-LARGE-1",
                    "title": "Software developer apprentice",
                    "fullDescription": "%s",
                    "addresses": [],
                    "skills": [],
                    "qualifications": []
                  }],
                  "total": 1,
                  "totalFiltered": 1,
                  "totalPages": 1
                }
                """.formatted(fullDescription)).getBytes(StandardCharsets.UTF_8);
        assertThat(response.length).isGreaterThan(256 * 1024);

        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/vacancies/vacancy", exchange -> {
            request.set(exchange);
            exchange.getResponseHeaders().set(HttpHeaders.CONTENT_TYPE,
                    "application/json; charset=utf-8; ver=2");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();

        ApprenticeshipsProperties properties = new ApprenticeshipsProperties();
        properties.setBaseUrl("http://" + server.getAddress().getHostString() + ":"
                + server.getAddress().getPort() + "/vacancies");
        properties.setApiKey("test-subscription-key");

        DfeVacancyPage result = new DfeDisplayAdvertClient(properties).fetchPage(3);

        assertThat(result.vacancies()).singleElement().satisfies(vacancy -> {
            assertThat(vacancy.vacancyReference()).isEqualTo("VAC-LARGE-1");
            assertThat(vacancy.fullDescription()).isEqualTo(fullDescription);
        });
        assertThat(request.get()).isNotNull();
        assertThat(request.get().getRequestMethod()).isEqualTo("GET");
        assertThat(request.get().getRequestURI().getPath()).isEqualTo("/vacancies/vacancy");
        assertThat(request.get().getRequestURI().getRawQuery())
                .contains("PageNumber=3", "PageSize=100", "IncludeDetails=true", "Sort=AgeDesc");
        assertThat(request.get().getRequestHeaders().getFirst(HttpHeaders.ACCEPT))
                .isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(request.get().getRequestHeaders().getFirst("X-Version")).isEqualTo("2");
        assertThat(request.get().getRequestHeaders().getFirst("Ocp-Apim-Subscription-Key"))
                .isEqualTo("test-subscription-key");
    }
}
