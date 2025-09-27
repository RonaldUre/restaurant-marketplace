// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/persistence/projection/ProductBasicProjection.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.projection;

/** Minimal product projection for cross-module reads (ownership + basics). */
public interface ProductBasicProjection {
    Long getProductId(); // p.id as productId
    String getSku();
    String getName();
    String getCategory();
}
