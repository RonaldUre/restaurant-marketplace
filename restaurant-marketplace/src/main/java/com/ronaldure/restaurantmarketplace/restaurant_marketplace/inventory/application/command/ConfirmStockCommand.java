package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command;

/** Used by Ordering after payment approval. Unlimited => no-op. */
public record ConfirmStockCommand(
        Long productId,
        Integer quantity
) { }
