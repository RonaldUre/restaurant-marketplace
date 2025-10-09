package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query;

import java.time.Instant;
import java.util.List;

/**
 * Parámetros de búsqueda para la lista de plataforma.
 * - page/size: paginación
 * - statuses: filtro por estados (OPEN, CLOSED, SUSPENDED). Si null o vacío => todos
 * - city: filtro exacto por ciudad (opcional)
 * - q: búsqueda simple por name/slug (opcional)
 * - sortBy: name | createdAt | status (default: createdAt)
 * - sortDir: asc | desc (default: desc)
 * - createdFrom/createdTo: rango opcional por fecha de creación (Instant, zona UTC)
 */
public record ListRestaurantsPlatformQueryParams(
        int page,
        int size,
        List<String> statuses,   // e.g., ["OPEN","CLOSED","SUSPENDED"]
        String city,             // exact match (opcional)
        String q,                // fulltext simple en name/slug (opcional)
        String sortBy,           // "createdAt" | "name" | "status"
        String sortDir,          // "asc" | "desc"
        Instant createdFrom,     // opcional
        Instant createdTo        // opcional
) {
    public String safeSortBy() {
        return switch (sortBy == null ? "createdAt" : sortBy) {
            case "name", "status", "createdAt" -> sortBy;
            default -> "createdAt";
        };
    }

    public String safeSortDir() {
        return ("asc".equalsIgnoreCase(sortDir)) ? "asc" : "desc";
    }
}
