package com.jobseekercopilot.apprenticeshipsgateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component @ConfigurationProperties(prefix = "apprenticeships")
public class ApprenticeshipsProperties {
    private String baseUrl = "https://api.apprenticeships.education.gov.uk/vacancies";
    private String apiKey;
    private int apiVersion = 2;
    private boolean enabled = true;
    private int pageSize = 100;
    private int maxPages = 150;
    private long syncDelayMs = 900_000;
    private long initialDelayMs = 1_000;
    public String getBaseUrl() { return baseUrl; } public void setBaseUrl(String value) { baseUrl = value; }
    public String getApiKey() { return apiKey; } public void setApiKey(String value) { apiKey = value; }
    public int getApiVersion() { return apiVersion; } public void setApiVersion(int value) { apiVersion = value; }
    public boolean isEnabled() { return enabled; } public void setEnabled(boolean value) { enabled = value; }
    public int getPageSize() { return Math.max(1, Math.min(100, pageSize)); } public void setPageSize(int value) { pageSize = value; }
    public int getMaxPages() { return Math.max(1, Math.min(150, maxPages)); } public void setMaxPages(int value) { maxPages = value; }
    public long getSyncDelayMs() { return syncDelayMs; } public void setSyncDelayMs(long value) { syncDelayMs = value; }
    public long getInitialDelayMs() { return initialDelayMs; } public void setInitialDelayMs(long value) { initialDelayMs = value; }
}
