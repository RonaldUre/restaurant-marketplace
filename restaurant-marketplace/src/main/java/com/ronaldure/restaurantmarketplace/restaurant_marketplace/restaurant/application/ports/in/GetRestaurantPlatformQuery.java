package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;

public interface GetRestaurantPlatformQuery {
    RestaurantView getById(Long id);
}
