package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query;

import java.util.Set;

public record ListPublishedProductsQueryParams(
        Long restaurantId,   // requerido
        String q,            // búsqueda simple por nombre
        String category,     // filtro único (opcional)
        int page,            // 0-based
        int size,            // > 0
        String sortBy,       // p.ej. "name" | "priceAmount"
        String sortDir       // "asc" | "desc"
) {
    public String safeSortBy(Set<String> allowed, String defaultField) {
        String candidate = (sortBy == null || sortBy.isBlank()) ? defaultField : sortBy;
        return (allowed != null && allowed.contains(candidate)) ? candidate : defaultField;
    }

    public String safeSortDir() {
        return "asc".equalsIgnoreCase(sortDir) ? "asc" : "desc";
    }
}