// .../application/view/ProductAdminDetailView.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductAdminDetailView(
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
