package com.dexinsights.backend.model;

public record Incident(
        String incidentId,
        String storeId,
        String timestamp,
        String severity,
        String category,
        String description,
        String status
) {
}