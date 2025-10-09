// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/web/dto/PublicProductDetailResponse.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.response;

import java.math.BigDecimal;

public record PublicProductDetailResponse(
        Long id,
        String name,
        String description,
        String category,
        BigDecimal priceAmount,
        String priceCurrency
) { }
