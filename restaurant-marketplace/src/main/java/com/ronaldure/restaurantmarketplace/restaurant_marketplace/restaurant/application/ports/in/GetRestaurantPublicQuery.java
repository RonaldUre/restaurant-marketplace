// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/ports/in/GetRestaurantPublicQuery.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query.GetRestaurantPublicQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;

/**
 * IN port para obtener el detalle público de un restaurante.
 * Política: si no existe, este caso de uso lanza RestaurantNotFoundException.
 */
public interface GetRestaurantPublicQuery {

    /**
     * Obtiene un restaurante por id o slug (XOR: exactamente uno).
     * @throws com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.RestaurantNotFoundException si no existe.
     * @throws IllegalArgumentException si no se cumple la regla XOR.
     */
    RestaurantView get(GetRestaurantPublicQueryParams params);
}