package com.dexinsights.backend.controller;

import com.dexinsights.backend.dto.OverviewResponse;
import com.dexinsights.backend.service.InsightService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InsightController {

    private final InsightService insightService;

    public InsightController(InsightService insightService) {
        this.insightService = insightService;
    }

    @GetMapping("/v1/insights/overview")
    public OverviewResponse getOverview() {
        return insightService.getOverview();
    }
}