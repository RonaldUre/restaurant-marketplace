package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * DTO de aplicación para detalle de pedido (dueño/admin).
 * Inmutable y serializable.
 */
public record OrderDetailView(
        Long id,
        Long tenantId,
        Long customerId,
        String status,            // CREATED | PAID | CANCELLED
        BigDecimal totalAmount,
        String currency,
        Instant createdAt,
        List<LineView> lines
) {
    public static OrderDetailView of(Long id, Long tenantId, Long customerId, String status,
                                     BigDecimal totalAmount, String currency,
                                     Instant createdAt, List<LineView> lines) {
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(totalAmount, "totalAmount");
        Objects.requireNonNull(currency, "currency");
        Objects.requireNonNull(createdAt, "createdAt");
        Objects.requireNonNull(lines, "lines");
        return new OrderDetailView(id, tenantId, customerId, status, totalAmount, currency, createdAt, List.copyOf(lines));
        // List.copyOf para asegurar inmutabilidad defensiva
    }

    /** Línea del pedido (snapshot). */
    public static record LineView(
            Long productId,
            String name,
            BigDecimal unitPriceAmount,
            String currency,
            int qty,
            BigDecimal lineTotalAmount
    ) { }
}
