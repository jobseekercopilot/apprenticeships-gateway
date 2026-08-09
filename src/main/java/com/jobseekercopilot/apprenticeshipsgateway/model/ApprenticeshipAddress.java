package com.jobseekercopilot.apprenticeshipsgateway.model;

import java.math.BigDecimal;

public record ApprenticeshipAddress(String addressLine1, String addressLine2, String addressLine3,
                                    String addressLine4, String postcode, BigDecimal latitude, BigDecimal longitude) {
    public String displayName() {
        return java.util.stream.Stream.of(addressLine1, addressLine2, addressLine3, addressLine4, postcode)
                .filter(value -> value != null && !value.isBlank()).distinct().collect(java.util.stream.Collectors.joining(", "));
    }
}
