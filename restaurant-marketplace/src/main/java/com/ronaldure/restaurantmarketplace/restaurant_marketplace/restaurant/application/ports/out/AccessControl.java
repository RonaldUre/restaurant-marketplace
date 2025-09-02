// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/ports/out/AccessControl.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out;

/**
 * OUT port for access control checks at the application layer.
 * Implement in infrastructure (e.g., Spring Security) and inject here.
 */
public interface AccessControl {

    /** @return true if the current user has the given role */
    boolean hasRole(String role);

    /** Convenience guard: throw if role is missing */
    default void requireRole(String role) {
        if (!hasRole(role)) {
            throw com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors
                    .ForbiddenOperationException.missingRole(role);
        }
    }
}
