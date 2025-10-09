package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security;

import java.util.Objects;

public record TenantId(Long value) {
    public TenantId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("TenantId must be positive");
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static TenantId of(Long raw) {
        return new TenantId(Objects.requireNonNull(raw, "TenantId is required"));
    }
}
