package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.view;

import java.time.Instant;

/** Admin view joining product basics + inventory state. */
public record InventoryAdminItemView(
        Long productId,
        String sku,
        String name,
        String category,
        Integer available,      // null => unlimited
        Integer reserved,       // never null
        boolean unlimited,      // convenience: available == null
        Instant createdAt,
        Instant updatedAt
) { }
