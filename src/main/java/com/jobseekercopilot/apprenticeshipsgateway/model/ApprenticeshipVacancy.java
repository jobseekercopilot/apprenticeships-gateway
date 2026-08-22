package com.jobseekercopilot.apprenticeshipsgateway.model;

import java.math.BigDecimal;
import java.util.List;

public record ApprenticeshipVacancy(
        String vacancyReference, String title, String description, String fullDescription,
        String employerName, String providerName, String postedDate, String closingDate, String startDate,
        BigDecimal wageAmount, String wageType, String wageUnit, String wageAdditionalInformation,
        String workingWeekDescription, BigDecimal hoursPerWeek, String expectedDuration, Integer numberOfPositions,
        List<ApprenticeshipAddress> addresses, String applicationUrl, String vacancyUrl,
        String courseTitle, Integer courseLevel, Integer courseLarsCode, String courseRoute, String courseType,
        String apprenticeshipLevel, Boolean nationalVacancy, String nationalVacancyDetails,
        List<String> skills, List<String> qualifications, String thingsToConsider, String companyBenefitsInformation) {}
