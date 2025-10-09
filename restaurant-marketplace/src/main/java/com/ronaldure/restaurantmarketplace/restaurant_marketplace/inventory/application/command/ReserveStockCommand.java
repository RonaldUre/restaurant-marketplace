// ReserveStockCommand.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command;

/** Used by Ordering. If unlimited, it's a no-op success. */
public record ReserveStockCommand(
        Long productId,
        Integer quantity
) { }
