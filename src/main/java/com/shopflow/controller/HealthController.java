package com.shopflow.controller;

import com.shopflow.config.AppProperties;
import com.shopflow.dto.HealthResponse;
import com.shopflow.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final HealthService healthService;
    private final AppProperties appProperties;

    public HealthController(HealthService healthService, AppProperties appProperties) {
        this.healthService = healthService;
        this.appProperties = appProperties;
    }

    @GetMapping("/api/health")
    public HealthResponse getHealth(){
        return new HealthResponse("OK",appProperties.getAppName(),appProperties.getVersion());
    }
}
