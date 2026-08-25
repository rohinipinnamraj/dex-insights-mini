package com.dexinsights.backend.controller;

import com.dexinsights.backend.model.Store;
import com.dexinsights.backend.service.StoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/v1/stores")
    public List<Store> getStores(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "false") boolean sortOfflinePumpsDesc) {

        return storeService.getStores(
                brand,
                status,
                sortOfflinePumpsDesc
        );
    }

    @GetMapping("/v1/stores/{storeId}")
    public Store getStoreById(@PathVariable String storeId) {
        return storeService.getStoreById(storeId);
    }
}