package com.jobseekercopilot.apprenticeshipsgateway.client;

import com.jobseekercopilot.apprenticeshipsgateway.model.ApprenticeshipAddress;
import com.jobseekercopilot.apprenticeshipsgateway.model.ApprenticeshipVacancy;
import com.jobseekercopilot.apprenticeshipsgateway.model.ApprenticeshipsSearchRequest;
import com.jobseekercopilot.apprenticeshipsgateway.model.ApprenticeshipsSearchResponse;
import com.jobseekercopilot.apprenticeshipsgateway.service.ApprenticeshipVacancyStore;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "external-provider", name = "mode", havingValue = "LIVE")
public class CachedApprenticeshipsProviderClient implements ApprenticeshipsProviderClient {
    private final ApprenticeshipVacancyStore store;
    public CachedApprenticeshipsProviderClient(ApprenticeshipVacancyStore store) { this.store = store; }
    @Override public ApprenticeshipsSearchResponse search(ApprenticeshipsSearchRequest request) {
        var snapshot = store.snapshot();
        int page = request.page() == null ? 1 : Math.max(1, request.page());
        int size = request.resultsPerPage() == null ? 20 : Math.max(1, Math.min(100, request.resultsPerPage()));
        List<ApprenticeshipVacancy> matches = snapshot.vacancies().stream().filter(this::open).filter(job -> keyword(job, request.targetRole()))
                .filter(job -> location(job, request)).toList();
        int from = Math.min(matches.size(), (page - 1) * size); int to = Math.min(matches.size(), from + size);
        boolean stale = snapshot.updatedAt() == null || Duration.between(snapshot.updatedAt(), Instant.now()).toHours() >= 1;
        return new ApprenticeshipsSearchResponse(matches.size(), page, size, snapshot.updatedAt(), stale, matches.subList(from, to));
    }
    private boolean open(ApprenticeshipVacancy job) {
        if (job.closingDate() == null || job.closingDate().isBlank()) return true;
        try { return !OffsetDateTime.parse(job.closingDate()).isBefore(OffsetDateTime.now(ZoneOffset.UTC)); }
        catch (RuntimeException ignored) { try { return !java.time.LocalDate.parse(job.closingDate().substring(0, 10)).isBefore(java.time.LocalDate.now(ZoneOffset.UTC)); } catch (RuntimeException ignoredAgain) { return true; } }
    }
    private boolean keyword(ApprenticeshipVacancy job, String query) {
        if (query == null || query.isBlank()) return true; String needle = query.toLowerCase(Locale.ROOT);
        return Stream.of(job.title(), job.description(), job.fullDescription(), job.employerName(), job.courseTitle())
                .filter(java.util.Objects::nonNull).anyMatch(value -> value.toLowerCase(Locale.ROOT).contains(needle));
    }
    private boolean location(ApprenticeshipVacancy job, ApprenticeshipsSearchRequest request) {
        if (Boolean.TRUE.equals(job.nationalVacancy())) return true;
        if (request.latitude() != null && request.longitude() != null && request.distanceMiles() != null)
            return job.addresses().stream().anyMatch(address -> within(address, request));
        if (request.location() == null || request.location().isBlank()) return true;
        String needle = request.location().toLowerCase(Locale.ROOT);
        return job.addresses().stream().anyMatch(address -> address.displayName().toLowerCase(Locale.ROOT).contains(needle));
    }
    private boolean within(ApprenticeshipAddress address, ApprenticeshipsSearchRequest request) {
        if (address.latitude() == null || address.longitude() == null) return false;
        double lat1 = Math.toRadians(request.latitude().doubleValue()), lat2 = Math.toRadians(address.latitude().doubleValue());
        double dLat = lat2 - lat1, dLon = Math.toRadians(address.longitude().doubleValue() - request.longitude().doubleValue());
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(lat1)*Math.cos(lat2)*Math.sin(dLon/2)*Math.sin(dLon/2);
        return 3958.7613 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)) <= request.distanceMiles();
    }
}
