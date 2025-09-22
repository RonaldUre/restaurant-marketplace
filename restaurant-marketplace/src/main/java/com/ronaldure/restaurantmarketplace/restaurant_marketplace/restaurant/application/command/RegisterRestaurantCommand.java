// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/command/RegisterRestaurantCommand.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command;

/**
 * Immutable input for RegisterRestaurantUseCase.
 * Validation here is "best-effort" to fail fast before touching the domain. 
 * Domain invariants are still enforced inside the aggregate.
 */
public record RegisterRestaurantCommand(
        String name,

        // kebab-case, same format expected by domain Slug VO
        String slug,

        // Optional contact info
        String email,

        String phone,

        // Optional address
        String addressLine1,
        String addressLine2,
        String city,
        String country, // ISO-3166-1 alpha-2
        String postalCode,

        // Optional JSON with opening hours (validated more strictly in domain if
        // needed)
        String openingHoursJson,

        String adminEmail,
        String adminPassword) {
    // You can add canonicalization helpers here later if needed.
}
