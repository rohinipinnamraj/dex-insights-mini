package com.dexinsights.backend;

import com.dexinsights.backend.dto.OverviewResponse;
import com.dexinsights.backend.model.Incident;
import com.dexinsights.backend.model.Store;
import com.dexinsights.backend.model.StoreAddress;
import com.dexinsights.backend.repository.DatasetRepository;
import com.dexinsights.backend.service.InsightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class InsightServiceTest {

    private DatasetRepository datasetRepository;
    private InsightService insightService;

    @BeforeEach
    void setUp() {

        datasetRepository = Mockito.mock(DatasetRepository.class);
        insightService = new InsightService(datasetRepository);

        List<Store> stores = List.of(
                new Store(
                        "10001",
                        "7-Eleven",
                        "ONLINE",
                        8,
                        8,
                        0,
                        false,
                        "2026-03-02T08:15:22.120Z",
                        new StoreAddress("TX", "Dallas"),
                        "32.7767",
                        "-96.7970",
                        0
                ),
                new Store(
                        "10002",
                        "7-Eleven",
                        "DEGRADED",
                        8,
                        5,
                        3,
                        false,
                        "2026-03-02T08:14:01.550Z",
                        new StoreAddress("TX", "Austin"),
                        "30.2672",
                        "-97.7431",
                        3
                ),
                new Store(
                        "10004",
                        "Speedway",
                        "OFFLINE",
                        12,
                        0,
                        12,
                        true,
                        "2026-03-02T07:59:44.300Z",
                        new StoreAddress("OH", "Columbus"),
                        "39.9612",
                        "-82.9988",
                        5
                )
        );

        List<Incident> incidents = List.of(
                new Incident(
                        "INC-001",
                        "10002",
                        "2026-03-02T06:12:00.000Z",
                        "HIGH",
                        "PUMP",
                        "Pump communication failure",
                        "OPEN"
                ),
                new Incident(
                        "INC-002",
                        "10004",
                        "2026-03-02T07:40:00.000Z",
                        "HIGH",
                        "NETWORK",
                        "Network outage",
                        "ACKNOWLEDGED"
                ),
                new Incident(
                        "INC-003",
                        "10002",
                        "2026-03-02T06:45:10.000Z",
                        "MEDIUM",
                        "ATG",
                        "ATG communication issue",
                        "RESOLVED"
                )
        );

        when(datasetRepository.getStores())
                .thenReturn(stores);

        when(datasetRepository.getIncidents())
                .thenReturn(incidents);
    }

    @Test
    void shouldReturnStoreWithHighestOfflinePumpsFirst() {

        OverviewResponse result = insightService.getOverview();

        assertEquals("10004",
                result.topOfflinePumpStores().get(0).storeId());

        assertEquals(12,
                result.topOfflinePumpStores().get(0).value());
    }

    @Test
    void shouldCountIncidentsBySeverity() {

        OverviewResponse result = insightService.getOverview();

        assertEquals(2L,
                result.incidentsBySeverity().get("HIGH"));

        assertEquals(1L,
                result.incidentsBySeverity().get("MEDIUM"));
    }

    @Test
    void shouldReturnStoreWithHighestAnomalyCountFirst() {

        OverviewResponse result = insightService.getOverview();

        assertEquals("10004",
                result.topAnomalyStores().get(0).storeId());

        assertEquals(5,
                result.topAnomalyStores().get(0).value());
    }
}