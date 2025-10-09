package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command.AdjustStockCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.view.InventoryAdminItemView;

public interface AdjustStockUseCase {
    InventoryAdminItemView adjust(AdjustStockCommand command);
}

