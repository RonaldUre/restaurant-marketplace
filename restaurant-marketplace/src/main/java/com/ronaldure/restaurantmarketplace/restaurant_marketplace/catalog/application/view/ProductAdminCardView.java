// .../application/view/ProductAdminCardView.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductAdminCardView(
        Long id,
        String sku,
        String name,
        String category,
        BigDecimal priceAmount,
        String priceCurrency,
        boolean published,
        Instant createdAt
) { }

