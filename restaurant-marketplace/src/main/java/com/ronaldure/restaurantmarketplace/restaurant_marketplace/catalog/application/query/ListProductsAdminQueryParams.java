// .../application/query/ListProductsAdminQueryParams.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query;

import java.time.Instant;
import java.util.Set;

public record ListProductsAdminQueryParams(
        String q,                 // search by name/sku
        Set<String> categories,   // multi-filter
        Boolean published,        // null = all
        Instant createdFrom,
        Instant createdTo,
int page,                  // 0-based
        int size,                  // > 0
        String sortBy,             // e.g. "createdAt" | "name" | "priceAmount" | "published"
        String sortDir             // "asc" | "desc"
) {
    /**
     * Normaliza el campo de ordenamiento contra una whitelist.
     * Si sortBy es null/blank o no está permitido, devuelve un valor por defecto.
     */
    public String safeSortBy(Set<String> allowed, String defaultField) {
        String candidate = (sortBy == null || sortBy.isBlank()) ? defaultField : sortBy;
        return (allowed != null && allowed.contains(candidate)) ? candidate : defaultField;
    }

    /**
     * Normaliza la dirección de ordenamiento. Default: "desc".
     */
    public String safeSortDir() {
        return "asc".equalsIgnoreCase(sortDir) ? "asc" : "desc";
    }
}
