package com.dexinsights.backend.service;

import com.dexinsights.backend.exception.StoreNotFoundException;
import com.dexinsights.backend.model.Store;
import com.dexinsights.backend.repository.DatasetRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class StoreService {

    private final DatasetRepository datasetRepository;

    public StoreService(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    public List<Store> getStores(
            String brand,
            String status,
            boolean sortOfflinePumpsDesc) {

        Stream<Store> stream = datasetRepository.getStores().stream();

        if (brand != null && !brand.isBlank()) {
            stream = stream.filter(store ->
                    store.brand().equalsIgnoreCase(brand));
        }

        if (status != null && !status.isBlank()) {
            stream = stream.filter(store ->
                    store.status().equalsIgnoreCase(status));
        }

        if (sortOfflinePumpsDesc) {
            stream = stream.sorted(
                    Comparator.comparingInt(Store::offlinePumps).reversed()
            );
        }

        return stream.toList();
    }

    public Store getStoreById(String storeId) {
        return datasetRepository.getStores()
                .stream()
                .filter(store -> store.storeId().equals(storeId))
                .findFirst()
                .orElseThrow(() -> new StoreNotFoundException(storeId));
    }
}