package com.jobseekercopilot.apprenticeshipsgateway.service;

import com.jobseekercopilot.apprenticeshipsgateway.model.ApprenticeshipVacancy;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;

@Component
public class ApprenticeshipVacancyStore {
    private final AtomicReference<Snapshot> snapshot = new AtomicReference<>(new Snapshot(List.of(), null));
    public Snapshot snapshot() { return snapshot.get(); }
    public void replace(List<ApprenticeshipVacancy> vacancies, Instant updatedAt) { snapshot.set(new Snapshot(List.copyOf(vacancies), updatedAt)); }
    public record Snapshot(List<ApprenticeshipVacancy> vacancies, Instant updatedAt) {}
}
