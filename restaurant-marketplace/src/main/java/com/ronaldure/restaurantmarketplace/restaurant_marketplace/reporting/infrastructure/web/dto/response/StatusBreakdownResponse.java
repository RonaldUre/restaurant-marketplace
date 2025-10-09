package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.web.dto.response;

/** Respuesta para distribución de pedidos por estado. */
public record StatusBreakdownResponse(
        String status, // "CREATED" | "PAID" | "CANCELLED"
        long count
) {}
