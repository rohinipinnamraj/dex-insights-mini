package com.dexinsights.backend.model;

public record Transaction(
        String transactionId,
        String storeId,
        String gradeName,
        String transactionAmnt,
        String volume,
        int dispenserId,
        String transactionStartTime,
        String transactionEndTime
) {
}