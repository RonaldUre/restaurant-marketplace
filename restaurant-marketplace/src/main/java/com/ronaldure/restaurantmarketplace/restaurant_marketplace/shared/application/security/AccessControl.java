package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.errors.ForbiddenOperationException;

public interface AccessControl {
    /** @return true if the current user has the given role */
    boolean hasRole(String role);

    /** Convenience guard: throw if role is missing */
    default void requireRole(String role) {
        if (!hasRole(role)) {
            throw ForbiddenOperationException
                    .missingRole(role);
        }
    }
}
