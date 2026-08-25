package com.dexinsights.backend.service;

import com.dexinsights.backend.dto.OverviewResponse;
import com.dexinsights.backend.dto.StoreInsight;
import com.dexinsights.backend.model.Incident;
import com.dexinsights.backend.model.Store;
import com.dexinsights.backend.repository.DatasetRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InsightService {

    private final DatasetRepository datasetRepository;

    public InsightService(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    public OverviewResponse getOverview() {

        List<StoreInsight> topOfflinePumpStores =
                datasetRepository.getStores()
                        .stream()
                        .sorted(
                                Comparator.comparingInt(Store::offlinePumps)
                                        .reversed()
                        )
                        .limit(5)
                        .map(store ->
                                new StoreInsight(
                                        store.storeId(),
                                        store.offlinePumps()
                                )
                        )
                        .toList();

        Map<String, Long> incidentsBySeverity =
                datasetRepository.getIncidents()
                        .stream()
                        .collect(
                                Collectors.groupingBy(
                                        Incident::severity,
                                        Collectors.counting()
                                )
                        );

        List<StoreInsight> topAnomalyStores =
                datasetRepository.getStores()
                        .stream()
                        .sorted(
                                Comparator.comparingInt(Store::anomalyCount)
                                        .reversed()
                        )
                        .limit(5)
                        .map(store ->
                                new StoreInsight(
                                        store.storeId(),
                                        store.anomalyCount()
                                )
                        )
                        .toList();

        return new OverviewResponse(
                topOfflinePumpStores,
                incidentsBySeverity,
                topAnomalyStores
        );
    }
}