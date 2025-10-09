package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.AuthenticatedUser;

public interface TokenDecoder {
    /** Decodes and validates a raw JWT string, returning the authenticated user. Implementations must throw a runtime exception on invalid/expired tokens. */
    AuthenticatedUser decode(String rawJwt);
}