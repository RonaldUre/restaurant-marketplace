// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/ports/in/RegisterRestaurantUseCase.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.RegisterRestaurantCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;

/**
 * Application boundary (IN port) for registering a new Restaurant (tenant).
 *
 * Transactional: yes (write).
 * Authorization: SUPER_ADMIN required.
 * Multitenancy: platform-level action (no tenant context).
 *
 * Returns a stable application DTO (RestaurantView), not the domain aggregate.
 */
public interface RegisterRestaurantUseCase {

    /**
     * Registers a new restaurant (defaults to CLOSED status).
     * Implementations must enforce:
     *  - slug uniqueness
     *  - basic validation
     *  - domain invariants via the aggregate
     */
    RestaurantView register(RegisterRestaurantCommand command);
}
