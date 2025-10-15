package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.query;

public record ListOrdersPublicQueryParams(
        String status,   // "PENDING" | "PAID" | "CANCELLED" | null
        int page,        // 0-based
        int size,        // > 0
        String sortBy,   // "createdAt" | "totalAmount" | "status"
        String sortDir   // "asc" | "desc"
) {
    public String safeSortBy() {
        return switch (sortBy == null ? "" : sortBy) {
            case "totalAmount", "status", "createdAt" -> sortBy;
            default -> "createdAt";
        };
    }
    public String safeSortDir() { return "asc".equalsIgnoreCase(sortDir) ? "asc" : "desc"; }
}
