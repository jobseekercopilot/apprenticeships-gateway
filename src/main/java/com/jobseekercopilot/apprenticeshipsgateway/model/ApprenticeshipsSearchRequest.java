package com.jobseekercopilot.apprenticeshipsgateway.model;

import java.math.BigDecimal;

public record ApprenticeshipsSearchRequest(String targetRole, String location, Integer distanceMiles,
        BigDecimal latitude, BigDecimal longitude, Integer postedWithinDays, Integer page, Integer resultsPerPage) {}
