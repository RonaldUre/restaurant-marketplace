package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;

/**
 * IN port (admin) para consultar el detalle del restaurante del tenant actual.
 * Auth: RESTAURANT_ADMIN
 */
public interface GetMyRestaurantDetailQuery {

    /**
     * Obtiene el detalle del restaurante del tenant actual.
     * @return RestaurantView del tenant
     * @throws com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.ForbiddenOperationException si no hay tenant
     * @throws com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.RestaurantNotFoundException si no existe
     */
    RestaurantView get();
}

