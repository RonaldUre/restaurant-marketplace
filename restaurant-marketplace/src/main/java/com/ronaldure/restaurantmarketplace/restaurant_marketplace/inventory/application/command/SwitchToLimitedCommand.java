// SwitchToLimitedCommand.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command;

/** Admin-only: set finite stock for an item currently unlimited. */
public record SwitchToLimitedCommand(
        Long productId,
        Integer initialAvailable   // >= 0
) { }

