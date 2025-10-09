// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/ports/in/SuspendRestaurantUseCase.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.SuspendRestaurantCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;

/**
 * IN port to suspend a restaurant (platform-level action).
 *
 * Transactional: yes (write).
 * Authorization: SUPER_ADMIN.
 * Multitenancy: not tied to caller's tenant; targets an explicit restaurant.
 */
public interface SuspendRestaurantUseCase {

    /**
     * Suspends the target restaurant (by id or slug).
     * @param command one-and-only-one of id or slug must be provided; reason optional.
     * @return Updated RestaurantView reflecting the SUSPENDED status.
     */
    RestaurantView suspend(SuspendRestaurantCommand command);
}
