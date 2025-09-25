package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command.ConfirmStockCommand;

public interface ConfirmStockUseCase {
    void confirm(ConfirmStockCommand command);
}