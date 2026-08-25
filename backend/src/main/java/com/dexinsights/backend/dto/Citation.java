package com.dexinsights.backend.dto;

public record Citation(
        String recordType,
        String recordId,
        String storeId,
        String timestamp
) {
}