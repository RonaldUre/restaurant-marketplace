// .../application/command/CreateProductCommand.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.command;

import java.math.BigDecimal;

public record CreateProductCommand(
        String sku,
        String name,
        String description,
        String category,
        BigDecimal priceAmount,
        String priceCurrency
) { }
