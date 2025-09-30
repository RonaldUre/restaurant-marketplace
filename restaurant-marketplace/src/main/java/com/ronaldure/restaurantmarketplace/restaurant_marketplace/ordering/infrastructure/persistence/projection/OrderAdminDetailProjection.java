// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/persistence/projection/OrderAdminDetailProjection.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.projection;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface OrderAdminDetailProjection {
    Long getId();
    Long getTenantId();
    Long getCustomerId();
    String getStatus();
    BigDecimal getTotalAmount();
    String getCurrency();
    Instant getCreatedAt();

    List<LineProjection> getLines();

    interface LineProjection {
        Long getProductId();
        String getProductName();
        BigDecimal getUnitPriceAmount();
        String getUnitPriceCurrency();
        int getQty();
        BigDecimal getLineTotalAmount();
    }
}
