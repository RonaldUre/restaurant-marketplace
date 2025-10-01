package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.web.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Respuesta para ventas diarias. */
public record DailySalesResponse(
        LocalDate date,
        long orders,
        BigDecimal totalAmount,
        String currency
) {}
