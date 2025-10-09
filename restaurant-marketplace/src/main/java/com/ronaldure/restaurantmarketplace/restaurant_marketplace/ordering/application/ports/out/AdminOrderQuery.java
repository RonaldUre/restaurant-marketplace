package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.query.ListOrdersAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;

public interface AdminOrderQuery {
    PageResponse<OrderCardView> list(TenantId tenantId, ListOrdersAdminQueryParams params, PageRequest page);
}
