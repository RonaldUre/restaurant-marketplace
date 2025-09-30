// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/persistence/projection/OrderBasicProjection.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.projection;

import java.math.BigDecimal;
import java.time.Instant;

public interface OrderBasicProjection {
    Long getId();
    Long getTenantId();
    Long getCustomerId();
    String getStatus();
    BigDecimal getTotalAmount();
    String getCurrency();
    Instant getCreatedAt();
}
