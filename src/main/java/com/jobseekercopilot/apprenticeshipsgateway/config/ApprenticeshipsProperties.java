package com.jobseekercopilot.apprenticeshipsgateway.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix = "apprenticeships")
public class ApprenticeshipsProperties {
    public static final int MIN_MAX_IN_MEMORY_RESPONSE_BYTES = 512 * 1024;
    public static final int DEFAULT_MAX_IN_MEMORY_RESPONSE_BYTES = 2 * 1024 * 1024;
    public static final int MAX_MAX_IN_MEMORY_RESPONSE_BYTES = 8 * 1024 * 1024;

    private String baseUrl = "https://api.apprenticeships.education.gov.uk/vacancies";
    private String apiKey;
    private int apiVersion = 2;
    private boolean enabled = true;
    private int pageSize = 100;
    private int maxPages = 150;
    @Min(MIN_MAX_IN_MEMORY_RESPONSE_BYTES)
    @Max(MAX_MAX_IN_MEMORY_RESPONSE_BYTES)
    private int maxInMemoryResponseBytes = DEFAULT_MAX_IN_MEMORY_RESPONSE_BYTES;
    private long syncDelayMs = 900_000;
    private long initialDelayMs = 1_000;
    public String getBaseUrl() { return baseUrl; } public void setBaseUrl(String value) { baseUrl = value; }
    public String getApiKey() { return apiKey; } public void setApiKey(String value) { apiKey = value; }
    public int getApiVersion() { return apiVersion; } public void setApiVersion(int value) { apiVersion = value; }
    public boolean isEnabled() { return enabled; } public void setEnabled(boolean value) { enabled = value; }
    public int getPageSize() { return Math.max(1, Math.min(100, pageSize)); } public void setPageSize(int value) { pageSize = value; }
    public int getMaxPages() { return Math.max(1, Math.min(150, maxPages)); } public void setMaxPages(int value) { maxPages = value; }
    public int getMaxInMemoryResponseBytes() { return maxInMemoryResponseBytes; } public void setMaxInMemoryResponseBytes(int value) { maxInMemoryResponseBytes = value; }
    public long getSyncDelayMs() { return syncDelayMs; } public void setSyncDelayMs(long value) { syncDelayMs = value; }
    public long getInitialDelayMs() { return initialDelayMs; } public void setInitialDelayMs(long value) { initialDelayMs = value; }
}
