package com.dexinsights.backend.dto;

import java.util.List;
import java.util.Map;

public record OverviewResponse(
        List<StoreInsight> topOfflinePumpStores,
        Map<String, Long> incidentsBySeverity,
        List<StoreInsight> topAnomalyStores
) {
}