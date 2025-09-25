// ReleaseStockCommand.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command;

/** Used by Ordering to release previously reserved qty. Unlimited => no-op. */
public record ReleaseStockCommand(
        Long productId,
        Integer quantity         // > 0

) { }
