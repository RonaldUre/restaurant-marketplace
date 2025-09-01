// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/ports/in/UpdateRestaurantProfileUseCase.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.UpdateRestaurantProfileCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;

/**
 * Application boundary (IN port) for updating the current tenant's Restaurant profile.
 *
 * Transactional: yes (write).
 * Authorization: RESTAURANT_ADMIN required.
 * Multitenancy: ignores any tenantId from the request; uses CurrentTenantProvider internally.
 *
 * Notes:
 *  - Null fields in the command mean "no change".
 *  - Slug uniqueness must be enforced by the implementation (repository + constraint).
 */
public interface UpdateRestaurantProfileUseCase {

    /**
     * Updates the profile fields for the current tenant's restaurant.
     * Returns the updated application view.
     */
    RestaurantView update(UpdateRestaurantProfileCommand command);
}
