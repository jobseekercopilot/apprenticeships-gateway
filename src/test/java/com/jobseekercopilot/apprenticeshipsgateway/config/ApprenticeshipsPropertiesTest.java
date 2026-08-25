package com.jobseekercopilot.apprenticeshipsgateway.config;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

class ApprenticeshipsPropertiesTest {
    @Test
    void responseDecodeLimitHasSafeDefaultAndValidatedBounds() {
        ApprenticeshipsProperties properties = new ApprenticeshipsProperties();
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            var validator = validatorFactory.getValidator();

            assertThat(properties.getMaxInMemoryResponseBytes())
                    .isEqualTo(ApprenticeshipsProperties.DEFAULT_MAX_IN_MEMORY_RESPONSE_BYTES);
            assertThat(validator.validate(properties)).isEmpty();

            properties.setMaxInMemoryResponseBytes(
                    ApprenticeshipsProperties.MIN_MAX_IN_MEMORY_RESPONSE_BYTES - 1);
            assertThat(validator.validate(properties))
                    .extracting(violation -> violation.getPropertyPath().toString())
                    .containsExactly("maxInMemoryResponseBytes");

            properties.setMaxInMemoryResponseBytes(
                    ApprenticeshipsProperties.MAX_MAX_IN_MEMORY_RESPONSE_BYTES + 1);
            assertThat(validator.validate(properties))
                    .extracting(violation -> violation.getPropertyPath().toString())
                    .containsExactly("maxInMemoryResponseBytes");
        }
    }
}
