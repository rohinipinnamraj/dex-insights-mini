package com.dexinsights.backend.exception;

public class StoreNotFoundException extends RuntimeException {

    public StoreNotFoundException(String storeId) {
        super("Store not found: " + storeId);
    }
}