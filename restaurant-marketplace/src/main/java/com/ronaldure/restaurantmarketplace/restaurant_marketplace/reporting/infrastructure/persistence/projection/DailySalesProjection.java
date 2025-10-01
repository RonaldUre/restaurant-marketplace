package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.persistence.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Proyección para ventas diarias por tenant. */
public interface DailySalesProjection {
    LocalDate getDate();
    Long getOrders();
    BigDecimal getTotalAmount();
    String getCurrency(); // ISO-4217 (p.ej. "EUR")
}
