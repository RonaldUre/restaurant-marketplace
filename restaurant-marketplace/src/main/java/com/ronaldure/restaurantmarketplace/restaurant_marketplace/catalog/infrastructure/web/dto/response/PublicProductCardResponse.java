package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.response;

import java.math.BigDecimal;

public record PublicProductCardResponse(
        Long id,
        String name,
        String category,
        BigDecimal priceAmount,
        String priceCurrency,
        boolean available               // ← NUEVO
) { }