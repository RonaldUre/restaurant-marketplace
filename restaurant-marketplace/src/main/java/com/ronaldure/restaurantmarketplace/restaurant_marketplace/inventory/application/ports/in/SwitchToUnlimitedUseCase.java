package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command.SwitchToUnlimitedCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.view.InventoryAdminItemView;

public interface SwitchToUnlimitedUseCase {
    InventoryAdminItemView switchToUnlimited(SwitchToUnlimitedCommand command);
}
