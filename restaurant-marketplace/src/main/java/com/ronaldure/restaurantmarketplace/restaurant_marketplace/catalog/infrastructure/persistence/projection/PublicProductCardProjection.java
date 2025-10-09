// PublicProductCardProjection.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.projection;

import java.math.BigDecimal;

/**
 * Public list/card projection.
 * Minimal fields for public catalog listing.
 */
public interface PublicProductCardProjection {
    Long getId();
    String getName();
    String getCategory();
    BigDecimal getPriceAmount();
    String getPriceCurrency();
}
