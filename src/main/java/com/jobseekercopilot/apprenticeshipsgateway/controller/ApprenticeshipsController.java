package com.jobseekercopilot.apprenticeshipsgateway.controller;

import com.jobseekercopilot.apprenticeshipsgateway.client.ApprenticeshipsProviderClient;
import com.jobseekercopilot.apprenticeshipsgateway.model.ApprenticeshipsSearchRequest;
import com.jobseekercopilot.apprenticeshipsgateway.model.ApprenticeshipsSearchResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/v1/apprenticeships/jobs")
public class ApprenticeshipsController {
    private final ApprenticeshipsProviderClient provider;
    public ApprenticeshipsController(ApprenticeshipsProviderClient provider) { this.provider = provider; }
    @PostMapping("/search") public ApprenticeshipsSearchResponse search(@RequestBody ApprenticeshipsSearchRequest request) { return provider.search(request); }
}
