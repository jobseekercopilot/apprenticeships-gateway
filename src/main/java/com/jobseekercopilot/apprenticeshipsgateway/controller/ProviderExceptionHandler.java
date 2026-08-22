package com.jobseekercopilot.apprenticeshipsgateway.controller;

import com.jobseekercopilot.apprenticeshipsgateway.client.DfeProviderException;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProviderExceptionHandler {
    @ExceptionHandler(DfeProviderException.class) ResponseEntity<Map<String,String>> failure(DfeProviderException exception) {
        String code = exception.getStatus().value() == 429 ? "RATE_LIMITED" : exception.getStatus().value() == 401 || exception.getStatus().value() == 403 ? "CONFIGURATION_ERROR" : "PROVIDER_UNAVAILABLE";
        return ResponseEntity.status(exception.getStatus()).body(Map.of("code", code, "message", "Find an apprenticeship is temporarily unavailable"));
    }
}
