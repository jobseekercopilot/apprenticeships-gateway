package com.jobseekercopilot.apprenticeshipsgateway.client;

import com.jobseekercopilot.apprenticeshipsgateway.model.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "external-provider", name = "mode", havingValue = "FIXTURE", matchIfMissing = true)
public class FixtureApprenticeshipsProviderClient implements ApprenticeshipsProviderClient {
    @Override public ApprenticeshipsSearchResponse search(ApprenticeshipsSearchRequest request) {
        // Deterministic fixture dates are derived relative to now so the advert
        // always remains open. A previously hard-coded closing date silently
        // expired once real time passed it, which filtered the fixture vacancy
        // out and broke the cached-search tests.
        Instant now = Instant.now();
        String postedDate = now.minus(java.time.Duration.ofDays(10)).toString();
        String closingDate = now.plus(java.time.Duration.ofDays(90)).toString();
        String startDate = now.plus(java.time.Duration.ofDays(120)).toString();
        var addresses = List.of(new ApprenticeshipAddress("Leeds Digital Hub", null, "Leeds", null, "LS1 2AB", new BigDecimal("53.8008"), new BigDecimal("-1.5491")),
                new ApprenticeshipAddress("Bradford Office", null, "Bradford", null, "BD1 1AA", new BigDecimal("53.7950"), new BigDecimal("-1.7594")));
        var job = new ApprenticeshipVacancy("VAC1000001", "Software Developer Apprentice", "Build and test accessible services.",
                "Work with an agile delivery team to build and test accessible services.", "Example Digital Ltd", "Example Training Provider",
                postedDate, closingDate, startDate, new BigDecimal("15000"),
                "FixedWage", "Annually", null, "Monday to Friday", new BigDecimal("37.5"), "18 months", 2, addresses,
                "https://www.findapprenticeship.service.gov.uk/apprenticeship/VAC1000001", "https://www.findapprenticeship.service.gov.uk/apprenticeship/VAC1000001",
                "Software developer (level 4)", 4, 123, "Digital", "Standard", "Higher", false, null,
                List.of("Team working", "Problem solving"), List.of("GCSE — English — 4 — Essential"), "Some hybrid working", "Training budget");
        int page = request.page() == null ? 1 : request.page(); int size = request.resultsPerPage() == null ? 10 : request.resultsPerPage();
        return new ApprenticeshipsSearchResponse(1, page, size, Instant.parse("2026-08-09T12:00:00Z"), false, List.of(job));
    }
}
