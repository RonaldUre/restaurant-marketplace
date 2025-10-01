package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.persistence.projection;

import java.math.BigDecimal;

/** Proyección para Top productos por revenue/cantidad. */
public interface TopProductProjection {
    Long getProductId();
    String getName();
    Long getQty();
    BigDecimal getRevenue();
    String getCurrency(); // ISO-4217
}
