package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;

public interface GetOrderPublicQuery {
    // Devuelve el pedido del "dueño" (se valida propiedad en el handler)
    OrderDetailView get(Long orderId);
}
