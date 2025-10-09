// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/persistence/projection/OrderAdminCardProjection.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.projection;

import java.math.BigDecimal;
import java.time.Instant;

public interface OrderAdminCardProjection {
    Long getId();
    String getStatus();
    BigDecimal getTotalAmount();
    String getCurrency();
    int getItemsCount();   // calculado via SUM(l.qty)
    Instant getCreatedAt();
}
