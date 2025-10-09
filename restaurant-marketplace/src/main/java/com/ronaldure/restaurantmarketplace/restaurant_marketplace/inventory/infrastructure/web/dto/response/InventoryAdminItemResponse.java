package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.web.dto.response;

import java.time.Instant;

/** Mirrors InventoryAdminItemView for admin listing/detail. */
public record InventoryAdminItemResponse(
        Long productId,
        String sku,
        String name,
        String category,
        Integer available,     // null => unlimited
        Integer reserved,      // never null
        boolean unlimited,     // convenience: available == null
        Instant createdAt,
        Instant updatedAt
) {}
