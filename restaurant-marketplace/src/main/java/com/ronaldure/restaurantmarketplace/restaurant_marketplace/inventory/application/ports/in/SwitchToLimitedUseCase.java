package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command.SwitchToLimitedCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.view.InventoryAdminItemView;

public interface SwitchToLimitedUseCase {
    InventoryAdminItemView switchToLimited(SwitchToLimitedCommand command);
}
