package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.AuthenticatedUser;
import java.util.Optional;

public interface CurrentUserProvider {
    Optional<AuthenticatedUser> findAuthenticated();

    default AuthenticatedUser requireAuthenticated() {
        return findAuthenticated().orElseThrow(() -> new IllegalStateException("Authentication required"));
    }
}