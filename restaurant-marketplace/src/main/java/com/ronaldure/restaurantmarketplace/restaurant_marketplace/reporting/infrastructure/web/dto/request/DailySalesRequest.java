package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Request para ventas diarias.
 * - Rango inclusivo [from..to] en días calendario.
 * - Admin: ignora tenantId del request y usa el del JWT.
 * - Platform: tenantId debe venir informado (validado en el controller/handler).
 */
public record DailySalesRequest(
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @Min(1) Long tenantId // opcional; requerido solo en plataforma
) {}
