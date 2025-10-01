package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.web.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Request para Top-N productos por revenue/cantidad.
 * - limit: normalizado a [1..100] en el handler (aquí solo validación básica).
 * - Admin vs Platform: misma regla de tenantId que en DailySalesRequest.
 */
public record TopProductsRequest(
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @Min(1) @Max(100) Integer limit,
        @Min(1) Long tenantId // opcional; requerido solo en plataforma
) {}
