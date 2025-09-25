// AdjustStockCommand.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command;

/** Admin-only. Positive delta increases available, negative decreases (limited only). */
public record AdjustStockCommand(
        Long productId,
        Integer delta,         
        String reason          // optional, can be null
) { }

