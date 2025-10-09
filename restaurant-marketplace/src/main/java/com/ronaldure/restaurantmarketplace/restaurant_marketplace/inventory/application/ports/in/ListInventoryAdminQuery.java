package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.query.ListInventoryAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.view.InventoryAdminItemView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;

public interface ListInventoryAdminQuery {
    PageResponse<InventoryAdminItemView> list(ListInventoryAdminQueryParams params);
}
