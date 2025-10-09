// .../application/view/PublicProductCardView.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view;

import java.math.BigDecimal;

public record PublicProductCardView(
        Long id,
        String name,
        String category,
        BigDecimal priceAmount,
        String priceCurrency
) { }
