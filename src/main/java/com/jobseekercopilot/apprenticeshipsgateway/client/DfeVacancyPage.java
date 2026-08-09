package com.jobseekercopilot.apprenticeshipsgateway.client;

import com.jobseekercopilot.apprenticeshipsgateway.model.ApprenticeshipVacancy;
import java.util.List;

public record DfeVacancyPage(List<ApprenticeshipVacancy> vacancies, int total, int totalFiltered, int totalPages) {}
