package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command;

/** Admin-only: require reserved == 0. */
public record SwitchToUnlimitedCommand(
        Long productId
) { }