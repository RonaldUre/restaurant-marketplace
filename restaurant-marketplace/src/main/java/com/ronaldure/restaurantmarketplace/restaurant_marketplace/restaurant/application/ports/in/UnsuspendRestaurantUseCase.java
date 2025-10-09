package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.UnsuspendRestaurantCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;

public interface UnsuspendRestaurantUseCase {
    /** Reactiva un restaurante: SUSPENDED -> CLOSED. Idempotente. */
    RestaurantView unsuspend(UnsuspendRestaurantCommand command);
}
