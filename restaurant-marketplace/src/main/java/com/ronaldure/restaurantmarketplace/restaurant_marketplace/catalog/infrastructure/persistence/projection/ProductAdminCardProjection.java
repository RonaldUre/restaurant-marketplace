// ProductAdminCardProjection.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.projection;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Admin list/grid projection.
 * Only the fields required by admin listing pages.
 */
public interface ProductAdminCardProjection {
    Long getId();
    String getSku();
    String getName();
    String getCategory();
    BigDecimal getPriceAmount();
    String getPriceCurrency();
    boolean isPublished();
    Instant getCreatedAt();
}
