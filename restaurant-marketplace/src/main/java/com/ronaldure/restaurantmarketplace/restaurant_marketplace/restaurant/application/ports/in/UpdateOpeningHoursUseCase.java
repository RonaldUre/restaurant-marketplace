package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.UpdateOpeningHoursCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;

/** Caso de uso (admin) para actualizar solo opening hours del tenant actual. */
public interface UpdateOpeningHoursUseCase {
    RestaurantView update(UpdateOpeningHoursCommand command);
}