package com.dexinsights.backend.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public record Store(

        @JsonAlias("STOREID")
        String storeId,

        @JsonAlias("BRAND")
        String brand,

        String status,
        int totalPumps,
        int activePumps,
        int offlinePumps,
        boolean hyperCare,
        String lastUpdatedTime,
        StoreAddress storeAddress,
        String latitude,
        String longitude,
        int anomalyCount
) {
}