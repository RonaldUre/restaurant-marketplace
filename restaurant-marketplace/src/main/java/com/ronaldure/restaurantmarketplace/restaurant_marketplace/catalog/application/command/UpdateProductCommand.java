// .../application/command/UpdateProductCommand.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.command;

import java.math.BigDecimal;

public record UpdateProductCommand(
        Long productId,
        String name,
        String description,
        String category,
        BigDecimal priceAmount,
        String priceCurrency
) { }
