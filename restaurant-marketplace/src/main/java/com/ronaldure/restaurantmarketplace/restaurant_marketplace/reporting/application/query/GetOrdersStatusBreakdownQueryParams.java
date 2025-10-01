package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.query;

import java.time.LocalDate;

/**
 * Query params for orders status breakdown.
 * - from/to: inclusive calendar days (validated in handler).
 * - tenantId: optional. If provided, requires SUPER_ADMIN; otherwise uses current tenant from JWT.
 */
public record GetOrdersStatusBreakdownQueryParams(
        LocalDate from,
        LocalDate to,
        Long tenantId // nullable
) {}
