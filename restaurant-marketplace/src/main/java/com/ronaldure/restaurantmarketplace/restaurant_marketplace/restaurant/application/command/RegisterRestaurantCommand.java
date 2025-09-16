// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/command/RegisterRestaurantCommand.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Immutable input for RegisterRestaurantUseCase.
 * Validation here is "best-effort" to fail fast before touching the domain. 
 * Domain invariants are still enforced inside the aggregate.
 */
public record RegisterRestaurantCommand(
        @NotBlank @Size(max = 120) String name,

        // kebab-case, same format expected by domain Slug VO
        @NotBlank @Size(max = 140) @Pattern(regexp = com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.validation.Patterns.SLUG) String slug,

        // Optional contact info
        @Email @Size(max = 255) String email,

        @Size(max = 30) String phone,

        // Optional address
        @Size(max = 255) String addressLine1,
        @Size(max = 255) String addressLine2,
        @Size(max = 120) String city,
        @Size(max = 2) String country, // ISO-3166-1 alpha-2
        @Size(max = 20) String postalCode,

        // Optional JSON with opening hours (validated more strictly in domain if
        // needed)
        String openingHoursJson,

        @NotBlank @Email @Size(max = 255) String adminEmail,
        @NotBlank @Size(min = 8, max = 100) String adminPassword) {
    // You can add canonicalization helpers here later if needed.
}
