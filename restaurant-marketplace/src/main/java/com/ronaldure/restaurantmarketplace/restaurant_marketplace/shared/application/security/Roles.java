package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security;

/**
 * Centralized role names used in access control checks.
 * Avoids scattering string literals across the codebase.
 */
public final class Roles {

    private Roles() {
        // prevent instantiation
    }

    // ----- Core roles -----
    public static final String RESTAURANT_ADMIN = "RESTAURANT_ADMIN";
    public static final String SUPER_ADMIN = "SUPER_ADMIN";
    public static final String CUSTOMER = "CUSTOMER";

    // (Optional) if you plan to add more, define them here:
    // public static final String SUPPORT = "SUPPORT";
}
