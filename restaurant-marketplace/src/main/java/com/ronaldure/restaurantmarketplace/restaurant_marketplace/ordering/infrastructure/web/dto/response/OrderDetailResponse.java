package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderDetailResponse(
        Long id,
        Long tenantId,
        Long customerId,
        String status,            // CREATED | PAID | CANCELLED
        BigDecimal totalAmount,
        String currency,
        Instant createdAt,
        List<LineResponse> lines
) {
    public static OrderDetailResponse from(OrderDetailView v) {
        Objects.requireNonNull(v, "view is required");
        List<LineResponse> mapped = v.lines().stream()
                .map(l -> new LineResponse(
                        l.productId(),
                        l.name(),
                        l.unitPriceAmount(),
                        l.currency(),
                        l.qty(),
                        l.lineTotalAmount()
                ))
                .toList();

        return new OrderDetailResponse(
                v.id(),
                v.tenantId(),
                v.customerId(),
                v.status(),
                v.totalAmount(),
                v.currency(),
                v.createdAt(),
                mapped
        );
    }

    public static record LineResponse(
            Long productId,
            String name,
            BigDecimal unitPriceAmount,
            String currency,
            int qty,
            BigDecimal lineTotalAmount
    ) { }
}
