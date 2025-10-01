package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.query;

import java.time.LocalDate;

/**
 * Query params for daily sales.
 * - from/to: inclusive calendar days (validated in handler).
 * - tenantId: optional. If provided, requires SUPER_ADMIN; otherwise uses current tenant from JWT.
 */
public record GetDailySalesQueryParams(
        LocalDate from,
        LocalDate to,
        Long tenantId // nullable
) {}
