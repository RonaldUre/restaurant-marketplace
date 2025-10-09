// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/ports/in/ListRestaurantsPublicQuery.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query.ListRestaurantsPublicQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;

/**
 * IN port para listar restaurantes públicos con paginación completa.
 */
public interface ListRestaurantsPublicQuery {

    /**
     * Devuelve items + totalElements + totalPages.
     */
    PageResponse<RestaurantCardView> list(ListRestaurantsPublicQueryParams params);
}
