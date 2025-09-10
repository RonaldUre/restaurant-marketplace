package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security;

public record UserId(String value) {
    public UserId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserId cannot be blank");
        }
    }
    @Override public String toString() { return value; }
}