// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/ports/in/CloseRestaurantUseCase.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;

/**
 * IN port to close the current tenant's restaurant.
 *
 * Transactional: yes (write).
 * Authorization: RESTAURANT_ADMIN.
 * Multitenancy: tenantId comes from CurrentTenantProvider (implementation).
 *
 * Idempotent: calling close() when already CLOSED should keep state and succeed.
 */
public interface CloseRestaurantUseCase {

    /**
     * Closes the current tenant's restaurant.
     * @return Updated RestaurantView reflecting the CLOSED status.
     */
    RestaurantView close();
}
