package com.dexinsights.backend.dto;

import java.util.List;

public record ChatResponse(
        String answer,
        List<Citation> citations,
        String retrievedContextSummary
) {
}