package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command.ReleaseStockCommand;

public interface ReleaseStockUseCase {
    void release(ReleaseStockCommand command);
}
