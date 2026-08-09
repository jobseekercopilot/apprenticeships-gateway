package com.jobseekercopilot.apprenticeshipsgateway.client;

import org.springframework.http.HttpStatus;

public class DfeProviderException extends RuntimeException {
    private final HttpStatus status;
    public DfeProviderException(String message, HttpStatus status) { super(message, null, false, false); this.status = status; }
    public HttpStatus getStatus() { return status; }
}
