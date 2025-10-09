// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/inventory/infrastructure/persistence/projection/InventoryAdminItemProjection.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.projection;

import java.time.Instant;

/**
 * Admin projection joining products + inventory.
 * - product basics from products
 * - stock fields from inventory
 */
public interface InventoryAdminItemProjection {
    Long getProductId();      // from products.id AS productId
    String getSku();          // from products.sku
    String getName();         // from products.name
    String getCategory();     // from products.category
    Integer getAvailable();   // from inventory.available (NULL => unlimited)
    Integer getReserved();    // from inventory.reserved
    Instant getCreatedAt();   // from inventory.created_at
    Instant getUpdatedAt();   // from inventory.updated_at
}
