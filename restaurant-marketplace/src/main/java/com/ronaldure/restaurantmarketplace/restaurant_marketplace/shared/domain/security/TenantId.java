package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security;

public record TenantId(String value) {
    public TenantId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TenantId cannot be blank");
        }
    }
    @Override public String toString() { return value; }
}