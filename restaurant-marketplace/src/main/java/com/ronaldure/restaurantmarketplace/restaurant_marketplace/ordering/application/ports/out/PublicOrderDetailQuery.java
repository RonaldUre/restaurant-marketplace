package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;

import java.util.Optional;

public interface PublicOrderDetailQuery {
    Optional<OrderDetailView> findOwned(Long orderId, UserId ownerId);
}

