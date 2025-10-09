package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view;

import java.math.BigDecimal;

public record PublicProductDetailView(
        Long id,
        String name,
        String description,
        String category,
        BigDecimal priceAmount,
        String priceCurrency
) { }
