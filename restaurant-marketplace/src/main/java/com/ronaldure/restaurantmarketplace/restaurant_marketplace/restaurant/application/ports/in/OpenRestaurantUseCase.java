// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/ports/in/OpenRestaurantUseCase.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;

/**
 * IN port to open the current tenant's restaurant.
 *
 * Transactional: yes (write).
 * Authorization: RESTAURANT_ADMIN.
 * Multitenancy: tenantId comes from CurrentTenantProvider (implementation).
 *
 * Idempotent: calling open() when already OPEN should keep state and succeed.
 */
public interface OpenRestaurantUseCase {

    /**
     * Opens the current tenant's restaurant.
     * @return Updated RestaurantView reflecting the OPEN status.
     */
    RestaurantView open();
}
