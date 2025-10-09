package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.query.ListInventoryAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.view.InventoryAdminItemView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;

public interface InventoryAdminQuery {
    PageResponse<InventoryAdminItemView> list(TenantId tenantId,
                                              ListInventoryAdminQueryParams params,
                                              PageRequest page);
}
