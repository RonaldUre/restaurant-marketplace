package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO de aplicación para listados (admin).
 */
public record OrderCardView(
        Long id,
        String status,           // CREATED | PAID | CANCELLED
        BigDecimal totalAmount,
        String currency,
        int itemsCount,
        Instant createdAt
) {
    public static OrderCardView of(Long id, String status, BigDecimal totalAmount,
                                   String currency, int itemsCount, Instant createdAt) {
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(totalAmount, "totalAmount");
        Objects.requireNonNull(currency, "currency");
        Objects.requireNonNull(createdAt, "createdAt");
        return new OrderCardView(id, status, totalAmount, currency, itemsCount, createdAt);
    }
}
