package com.jobseekercopilot.apprenticeshipsgateway.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.env.Environment;

class ProviderModeSafetyTest {
    @Test
    void liveModeRequiresASecretAtStartup() {
        var external = new ExternalProviderProperties();
        external.setMode(ExternalProviderMode.LIVE);
        var provider = new ApprenticeshipsProperties();
        Environment environment = mock(Environment.class);
        when(environment.getActiveProfiles()).thenReturn(new String[0]);

        assertThatThrownBy(() -> new ProviderModeSafety(external, provider, environment).run(mock(ApplicationArguments.class)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("APPRENTICESHIPS_API_KEY");
    }
}
