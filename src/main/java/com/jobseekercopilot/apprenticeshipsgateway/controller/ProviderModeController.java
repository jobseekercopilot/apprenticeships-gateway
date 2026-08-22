package com.jobseekercopilot.apprenticeshipsgateway.controller;

import com.jobseekercopilot.apprenticeshipsgateway.config.*;
import com.jobseekercopilot.apprenticeshipsgateway.service.ApprenticeshipVacancyStore;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/internal")
public class ProviderModeController {
    private final ExternalProviderProperties external; private final ApprenticeshipsProperties provider; private final ApprenticeshipVacancyStore store;
    public ProviderModeController(ExternalProviderProperties external, ApprenticeshipsProperties provider, ApprenticeshipVacancyStore store) { this.external=external; this.provider=provider; this.store=store; }
    @GetMapping("/provider-mode") public Map<String,Object> mode() {
        Map<String,Object> result = new LinkedHashMap<>(); result.put("gateway","apprenticeships-gateway"); result.put("mode", external.getMode().name());
        result.put("externalCallsEnabled", external.getMode() == ExternalProviderMode.LIVE && provider.isEnabled()); result.put("apiVersion", provider.getApiVersion());
        result.put("cachedVacancies", store.snapshot().vacancies().size()); result.put("snapshotUpdatedAt", store.snapshot().updatedAt()); return result;
    }
}
