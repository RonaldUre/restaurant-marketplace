package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.response;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderCardView;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record OrderCardResponse(
        Long id,
        String status,
        BigDecimal totalAmount,
        String currency,
        int itemsCount,
        Instant createdAt
) {
    public static OrderCardResponse from(OrderCardView v) {
        Objects.requireNonNull(v, "view is required");
        return new OrderCardResponse(
                v.id(),
                v.status(),
                v.totalAmount(),
                v.currency(),
                v.itemsCount(),
                v.createdAt()
        );
    }
}
