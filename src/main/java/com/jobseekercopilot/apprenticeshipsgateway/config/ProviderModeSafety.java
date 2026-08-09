package com.jobseekercopilot.apprenticeshipsgateway.config;

import java.util.Arrays;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ProviderModeSafety implements ApplicationRunner {
    private final ExternalProviderProperties external;
    private final ApprenticeshipsProperties provider;
    private final Environment environment;
    public ProviderModeSafety(ExternalProviderProperties external, ApprenticeshipsProperties provider, Environment environment) {
        this.external = external; this.provider = provider; this.environment = environment;
    }
    @Override public void run(ApplicationArguments args) {
        boolean production = Arrays.stream(environment.getActiveProfiles()).anyMatch(value -> value.equalsIgnoreCase("prod") || value.equalsIgnoreCase("production"));
        if (production && external.getMode() == ExternalProviderMode.FIXTURE) throw new IllegalStateException("apprenticeships-gateway cannot start in FIXTURE mode with a production profile");
        if (external.getMode() == ExternalProviderMode.LIVE && provider.isEnabled() && (provider.getApiKey() == null || provider.getApiKey().isBlank()))
            throw new IllegalStateException("APPRENTICESHIPS_API_KEY is required when the live provider is enabled");
        if (provider.getApiVersion() != 2) throw new IllegalStateException("Only Display Advert API version 2 is supported");
    }
}
