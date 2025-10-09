// ProductAdminDetailProjection.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.projection;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Admin detail projection.
 * Richer dataset for admin detail pages (includes sku and timestamps).
 */
public interface ProductAdminDetailProjection {
    Long getId();
    String getSku();
    String getName();
    String getDescription();
    String getCategory();
    BigDecimal getPriceAmount();
    String getPriceCurrency();
    boolean isPublished();
    Instant getCreatedAt();
    Instant getUpdatedAt();
}

