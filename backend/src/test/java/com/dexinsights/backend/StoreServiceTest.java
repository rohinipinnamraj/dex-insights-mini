package com.dexinsights.backend;

import com.dexinsights.backend.model.Store;
import com.dexinsights.backend.model.StoreAddress;
import com.dexinsights.backend.repository.DatasetRepository;
import com.dexinsights.backend.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class StoreServiceTest {

    private DatasetRepository datasetRepository;
    private StoreService storeService;

    @BeforeEach
    void setUp() {

        datasetRepository = Mockito.mock(DatasetRepository.class);

        storeService = new StoreService(datasetRepository);

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

        when(datasetRepository.getStores())
                .thenReturn(stores);
    }

    @Test
    void shouldFilterStoresByStatus() {

        List<Store> result =
                storeService.getStores(
                        null,
                        "DEGRADED",
                        false
                );

        assertEquals(1, result.size());
        assertEquals("10002", result.get(0).storeId());
    }

    @Test
    void shouldFilterStoresByBrand() {

        List<Store> result =
                storeService.getStores(
                        "7-Eleven",
                        null,
                        false
                );

        assertEquals(2, result.size());
    }

    @Test
    void shouldSortStoresByOfflinePumpsDescending() {

        List<Store> result =
                storeService.getStores(
                        null,
                        null,
                        true
                );

        assertEquals("10004", result.get(0).storeId());
        assertEquals(12, result.get(0).offlinePumps());
    }

    @Test
    void shouldFindStoreById() {

        Store result =
                storeService.getStoreById("10001");

        assertEquals("10001", result.storeId());
        assertEquals("ONLINE", result.status());
    }
}