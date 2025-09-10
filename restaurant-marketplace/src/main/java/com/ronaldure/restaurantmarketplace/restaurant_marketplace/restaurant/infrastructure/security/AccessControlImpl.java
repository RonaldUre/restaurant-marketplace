package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.security;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.ForbiddenOperationException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentUserProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.AuthenticatedUser;

public class AccessControlImpl implements AccessControl {

    private final CurrentUserProvider currentUserProvider;

    public AccessControlImpl(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public boolean hasRole(String role) {
        AuthenticatedUser user = currentUserProvider.requireAuthenticated();
        return user.roles().stream().anyMatch(r -> r.name().equals(role));
    }

    @Override
    public void requireRole(String role) {
        if (!hasRole(role)) {
            throw ForbiddenOperationException.missingRole(role);
        }
    }
}