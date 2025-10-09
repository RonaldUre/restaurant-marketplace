package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.query;

import java.time.Instant;
import java.util.Set;

/**
 * Parámetros de filtrado/ordenación para listados admin de pedidos.
 */
public record ListOrdersAdminQueryParams(
        String status,         // "CREATED" | "PAID" | "CANCELLED" | null = todos
        Long customerId,       // opcional
        Instant createdFrom,   // opcional
        Instant createdTo,     // opcional
        int page,              // 0-based
        int size,              // > 0
        String sortBy,         // "createdAt" | "totalAmount" | "status"
        String sortDir         // "asc" | "desc"
) {
    public String safeSortBy(Set<String> allowed, String defaultField) {
        String candidate = (sortBy == null || sortBy.isBlank()) ? defaultField : sortBy;
        return (allowed != null && allowed.contains(candidate)) ? candidate : defaultField;
    }

    public String safeSortDir() {
        return "asc".equalsIgnoreCase(sortDir) ? "asc" : "desc";
    }
}
