package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;

public interface GetOrderAdminQuery {
    // Detalle para admin del tenant
    OrderDetailView get(Long orderId);
}
