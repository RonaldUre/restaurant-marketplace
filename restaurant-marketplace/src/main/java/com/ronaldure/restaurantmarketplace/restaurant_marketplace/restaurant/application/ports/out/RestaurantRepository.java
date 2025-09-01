// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/ports/out/RestaurantRepository.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.Restaurant;

import java.util.Optional;

/**
 * OUT port for persisting and retrieving Restaurant aggregates.
 * Implemented by infrastructure (e.g., JPA adapter).
 */
public interface RestaurantRepository {

    Optional<Restaurant> findById(Long id);

    Optional<Restaurant> findBySlug(String slug);

    /**
     * Saves a Restaurant aggregate.
     * Implementations should assign an ID to new aggregates when persisting.
     */
    Restaurant save(Restaurant restaurant);

    /**
     * Checks if a slug is already in use (for uniqueness).
     */
    boolean existsBySlug(String slug);
}
