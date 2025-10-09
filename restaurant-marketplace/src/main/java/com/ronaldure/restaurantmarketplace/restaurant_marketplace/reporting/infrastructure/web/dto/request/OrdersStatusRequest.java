package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Request para distribución de pedidos por estado.
 * - Rango inclusivo [from..to] en días calendario.
 * - Admin vs Platform: misma regla de tenantId.
 */
public record OrdersStatusRequest(
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @Min(1) Long tenantId // opcional; requerido solo en plataforma
) {}
