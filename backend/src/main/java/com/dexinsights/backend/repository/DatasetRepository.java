package com.dexinsights.backend.repository;

import com.dexinsights.backend.model.Incident;
import com.dexinsights.backend.model.Store;
import com.dexinsights.backend.model.Transaction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class DatasetRepository {

    private final ObjectMapper objectMapper;

    private List<Store> stores;
    private List<Transaction> transactions;
    private List<Incident> incidents;

    public DatasetRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void loadData() throws Exception {

        InputStream storesStream =
                getClass().getResourceAsStream("/data/stores.json");

        InputStream transactionsStream =
                getClass().getResourceAsStream("/data/transactions.json");

        InputStream incidentsStream =
                getClass().getResourceAsStream("/data/incidents.json");

        if (storesStream == null ||
                transactionsStream == null ||
                incidentsStream == null) {
            throw new IllegalStateException("Dataset files not found");
        }

        stores = objectMapper.readValue(
                storesStream,
                new TypeReference<List<Store>>() {}
        );

        transactions = objectMapper.readValue(
                transactionsStream,
                new TypeReference<List<Transaction>>() {}
        );

        incidents = objectMapper.readValue(
                incidentsStream,
                new TypeReference<List<Incident>>() {}
        );

        System.out.println("Loaded stores: " + stores.size());
        System.out.println("Loaded transactions: " + transactions.size());
        System.out.println("Loaded incidents: " + incidents.size());
    }

    public List<Store> getStores() {
        return stores;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Incident> getIncidents() {
        return incidents;
    }
}