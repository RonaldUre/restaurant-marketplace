// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/ports/out/PublicRestaurantQuery.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;

import java.util.Optional;

/**
 * OUT port for lightweight public read models (CQRS-light).
 * Implementations may use optimized SQL/JPA DTO projections without touching the aggregate.
 */
public interface PublicRestaurantQuery {

    /**
     * Public listing for marketplace browsing.
     * Should typically return only OPEN restaurants, with optional filters applied by the adapter.
     */
    PageResponse<RestaurantCardView> listPublic(PageRequest page, String cityFilter);

    /**
     * Public detail by slug (preferred) or id.
     * Implementations can expose two overloads for convenience.
     */
    Optional<RestaurantView> getBySlug(String slug);

    Optional<RestaurantView> getById(Long id);
}
