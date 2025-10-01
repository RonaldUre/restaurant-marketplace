package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.query;

import java.time.LocalDate;

/**
 * Query params for top products.
 * - from/to: inclusive calendar days (validated in handler).
 * - limit: top N (handler normalizes to [1..100], default 10 if null/invalid).
 * - tenantId: optional. If provided, requires SUPER_ADMIN; otherwise uses current tenant from JWT.
 */
public record GetTopProductsQueryParams(
        LocalDate from,
        LocalDate to,
        Integer limit, // nullable; default handled in handler
        Long tenantId  // nullable
) {}
