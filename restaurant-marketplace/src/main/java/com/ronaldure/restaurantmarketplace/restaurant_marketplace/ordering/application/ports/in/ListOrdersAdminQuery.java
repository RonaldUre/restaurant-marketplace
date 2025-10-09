package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.query.ListOrdersAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;

public interface ListOrdersAdminQuery {
    PageResponse<OrderCardView> list(ListOrdersAdminQueryParams params);
}
