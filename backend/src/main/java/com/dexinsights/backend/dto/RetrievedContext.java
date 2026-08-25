package com.dexinsights.backend.dto;

import com.dexinsights.backend.model.Incident;
import com.dexinsights.backend.model.Store;
import com.dexinsights.backend.model.Transaction;

import java.util.List;

public record RetrievedContext(
        List<Store> stores,
        List<Incident> incidents,
        List<Transaction> transactions
) {
}