package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.query;

import java.util.Set;

public record ListInventoryAdminQueryParams(
        String sku,             // optional exact/like as adapter decides
        Long productId,         // optional
        String category,        // optional
        int page,               // 0-based
        int size,               // > 0
        String sortBy,          // "name" | "sku" | "category" | "available" | "reserved" | "updatedAt"
        String sortDir          // "asc" | "desc"
) {
    public String safeSortBy(Set<String> allowed, String def) {
        String candidate = (sortBy == null || sortBy.isBlank()) ? def : sortBy;
        return (allowed != null && allowed.contains(candidate)) ? candidate : def;
    }
    public String safeSortDir() {
        return "asc".equalsIgnoreCase(sortDir) ? "asc" : "desc";
    }
}
