// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/ports/in/ListRestaurantsPublicQuery.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query.ListRestaurantsPublicQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantCardView;

import java.util.List;

/**
 * IN port to list restaurants for public marketplace browsing.
 *
 * Transactional: no (read-only).
 * Authorization: none (public).
 * Multitenancy: none (global view, tenantId not required).
 *
 * Optimized for lightweight "cards" (id, name, slug, status, city).
 */
public interface ListRestaurantsPublicQuery {

    /**
     * Returns a paginated list of restaurant cards.
     */
    List<RestaurantCardView> list(ListRestaurantsPublicQueryParams params);
}
