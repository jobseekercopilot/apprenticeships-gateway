package com.jobseekercopilot.apprenticeshipsgateway.service;

import com.jobseekercopilot.apprenticeshipsgateway.client.DfeDisplayAdvertClient;
import com.jobseekercopilot.apprenticeshipsgateway.client.DfeVacancyPage;
import com.jobseekercopilot.apprenticeshipsgateway.config.ApprenticeshipsProperties;
import com.jobseekercopilot.apprenticeshipsgateway.model.ApprenticeshipVacancy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "external-provider", name = "mode", havingValue = "LIVE")
public class VacancySynchroniser {
    private static final Logger log = LoggerFactory.getLogger(VacancySynchroniser.class);
    private final DfeDisplayAdvertClient client; private final ApprenticeshipVacancyStore store; private final ApprenticeshipsProperties properties;
    public VacancySynchroniser(DfeDisplayAdvertClient client, ApprenticeshipVacancyStore store, ApprenticeshipsProperties properties) {
        this.client = client; this.store = store; this.properties = properties;
    }
    @Scheduled(fixedDelayString = "${apprenticeships.sync-delay-ms:900000}", initialDelayString = "${apprenticeships.initial-delay-ms:1000}")
    public void synchronise() {
        if (!properties.isEnabled()) return;
        List<ApprenticeshipVacancy> next = new ArrayList<>();
        try {
            DfeVacancyPage first = client.fetchPage(1); next.addAll(first.vacancies());
            int pages = Math.min(Math.max(1, first.totalPages()), properties.getMaxPages());
            for (int page = 2; page <= pages; page++) next.addAll(client.fetchPage(page).vacancies());
            store.replace(next, Instant.now());
            log.info("Apprenticeship vacancy snapshot refreshed vacancies={} pages={}", next.size(), pages);
        } catch (RuntimeException exception) {
            log.warn("Apprenticeship vacancy refresh failed; retaining previous snapshot error={}", exception.getClass().getSimpleName());
        }
    }
}
