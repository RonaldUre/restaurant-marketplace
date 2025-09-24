// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/web/dto/ProductAdminCardResponse.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductAdminCardResponse(
        Long id,
        String sku,
        String name,
        String category,
        BigDecimal priceAmount,
        String priceCurrency,
        boolean published,
        Instant createdAt
) { }
