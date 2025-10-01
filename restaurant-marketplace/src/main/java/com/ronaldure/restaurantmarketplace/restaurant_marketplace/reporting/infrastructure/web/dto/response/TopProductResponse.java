package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.web.dto.response;

import java.math.BigDecimal;

/** Respuesta para Top productos. */
public record TopProductResponse(
        long productId,
        String name,
        long qty,
        BigDecimal revenue,
        String currency
) {}
