package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.persistence.projection;

/** Proyección para distribución de pedidos por estado. */
public interface StatusBreakdownProjection {
    String getStatus(); // "CREATED" | "PAID" | "CANCELLED"
    Long getCount();
}
