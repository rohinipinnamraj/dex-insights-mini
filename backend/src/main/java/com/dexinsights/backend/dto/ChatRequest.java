package com.dexinsights.backend.dto;

public record ChatRequest(
        String question,
        String storeId
) {
}