// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/web/dto/ProductAdminDetailResponse.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductAdminDetailResponse(
        Long id,
        String sku,
        String name,
        String description,
        String category,
        BigDecimal priceAmount,
        String priceCurrency,
        boolean published,
        Instant createdAt,
        Instant updatedAt
) { }
