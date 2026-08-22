package com.jobseekercopilot.apprenticeshipsgateway.model;

import java.time.Instant;
import java.util.List;

public record ApprenticeshipsSearchResponse(String provider, int totalAvailable, int page, int resultsPerPage,
        Instant snapshotUpdatedAt, boolean stale, List<ApprenticeshipVacancy> jobs) {
    public ApprenticeshipsSearchResponse(int total, int page, int size, Instant updatedAt, boolean stale, List<ApprenticeshipVacancy> jobs) {
        this("APPRENTICESHIPS", total, page, size, updatedAt, stale, jobs);
    }
}
