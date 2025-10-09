// PublicProductDetailProjection.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.projection;

import java.math.BigDecimal;

/**
 * Public detail projection.
 * Include description but no tenant/sensitive fields.
 */
public interface PublicProductDetailProjection {
    Long getId();
    String getName();
    String getDescription();
    String getCategory();
    BigDecimal getPriceAmount();
    String getPriceCurrency();
}
