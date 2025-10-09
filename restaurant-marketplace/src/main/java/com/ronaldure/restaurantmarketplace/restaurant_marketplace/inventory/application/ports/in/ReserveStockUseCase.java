package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command.ReserveStockCommand;

/** Ordering-facing port. Returns void to keep semantics simple; throw on failure. */
public interface ReserveStockUseCase {
    void reserve(ReserveStockCommand command);
}
