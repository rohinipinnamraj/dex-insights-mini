package com.dexinsights.backend.service;

import com.dexinsights.backend.dto.RetrievedContext;
import com.dexinsights.backend.model.Incident;
import com.dexinsights.backend.model.Store;
import com.dexinsights.backend.model.Transaction;
import com.dexinsights.backend.repository.DatasetRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ChatRetrievalService {

    private final DatasetRepository datasetRepository;

    private static final Pattern STORE_ID_PATTERN =
            Pattern.compile("\\b\\d{5}\\b");

    public ChatRetrievalService(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    public RetrievedContext retrieve(String question, String requestedStoreId) {

        String normalizedQuestion =
                question == null ? "" : question.toLowerCase();

        String storeId = requestedStoreId;

        // If storeId was not supplied separately,
        // try to find it inside the question.
        if (storeId == null || storeId.isBlank()) {
            storeId = extractStoreId(question);
        }

        // Store-specific question
        if (storeId != null && !storeId.isBlank()) {
            return retrieveForStore(storeId);
        }

        // Question about highest offline pumps
        if (normalizedQuestion.contains("offline")
                && normalizedQuestion.contains("pump")) {

            return retrieveOfflinePumpContext();
        }

        // General incident-related question
        if (normalizedQuestion.contains("incident")) {
            return retrieveIncidentContext();
        }

        return retrieveGeneralContext();
    }

    private RetrievedContext retrieveForStore(String storeId) {

        List<Store> stores = datasetRepository.getStores()
                .stream()
                .filter(store -> store.storeId().equals(storeId))
                .toList();

        List<Incident> incidents = datasetRepository.getIncidents()
                .stream()
                .filter(incident -> incident.storeId().equals(storeId))
                .sorted(
                        Comparator.comparing(Incident::timestamp)
                                .reversed()
                )
                .limit(5)
                .toList();

        List<Transaction> transactions =
                datasetRepository.getTransactions()
                        .stream()
                        .filter(transaction ->
                                transaction.storeId().equals(storeId))
                        .sorted(
                                Comparator.comparing(
                                        Transaction::transactionStartTime
                                ).reversed()
                        )
                        .limit(5)
                        .toList();

        return new RetrievedContext(
                stores,
                incidents,
                transactions
        );
    }

    private RetrievedContext retrieveOfflinePumpContext() {

        List<Store> stores = datasetRepository.getStores()
                .stream()
                .filter(store -> store.offlinePumps() > 0)
                .sorted(
                        Comparator.comparingInt(Store::offlinePumps)
                                .reversed()
                )
                .limit(3)
                .toList();

        Set<String> storeIds = stores.stream()
                .map(Store::storeId)
                .collect(Collectors.toSet());

        List<Incident> incidents = datasetRepository.getIncidents()
                .stream()
                .filter(incident ->
                        storeIds.contains(incident.storeId()))
                .sorted(
                        Comparator.comparing(Incident::timestamp)
                                .reversed()
                )
                .limit(10)
                .toList();

        return new RetrievedContext(
                stores,
                incidents,
                List.of()
        );
    }

    private RetrievedContext retrieveIncidentContext() {

        List<Incident> incidents = datasetRepository.getIncidents()
                .stream()
                .sorted(
                        Comparator.comparing(Incident::timestamp)
                                .reversed()
                )
                .limit(10)
                .toList();

        Set<String> storeIds = incidents.stream()
                .map(Incident::storeId)
                .collect(Collectors.toSet());

        List<Store> stores = datasetRepository.getStores()
                .stream()
                .filter(store ->
                        storeIds.contains(store.storeId()))
                .toList();

        return new RetrievedContext(
                stores,
                incidents,
                List.of()
        );
    }

    private RetrievedContext retrieveGeneralContext() {

        List<Store> stores = datasetRepository.getStores()
                .stream()
                .sorted(
                        Comparator.comparingInt(Store::anomalyCount)
                                .reversed()
                )
                .limit(5)
                .toList();

        List<Incident> incidents = datasetRepository.getIncidents()
                .stream()
                .sorted(
                        Comparator.comparing(Incident::timestamp)
                                .reversed()
                )
                .limit(5)
                .toList();

        return new RetrievedContext(
                stores,
                incidents,
                List.of()
        );
    }

    private String extractStoreId(String question) {

        if (question == null) {
            return null;
        }

        Matcher matcher = STORE_ID_PATTERN.matcher(question);

        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }
}