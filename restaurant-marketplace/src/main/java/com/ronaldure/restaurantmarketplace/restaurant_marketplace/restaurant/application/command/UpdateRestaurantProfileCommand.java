// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/command/UpdateRestaurantProfileCommand.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Immutable input for UpdateRestaurantProfileUseCase.
 *
 * All fields are optional:
 *  - null => "no change" (the service will keep current value)
 *  - non-null => validate and apply
 *
 * Validation here is best-effort; domain VOs (Name, Slug, Email, etc.) still enforce invariants.
 */
public record UpdateRestaurantProfileCommand(

        // Optional human-readable name (same constraints as domain Name VO)
        @Size(min = 1, max = 120)
        String name,

        // Optional slug in kebab-case; must match domain Slug VO format if provided
        @Size(min = 1, max = 140)
        @Pattern(regexp = com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.validation.Patterns.SLUG)
        String slug,

        // Optional contact
        @Email @Size(max = 255)
        String email,

        @Size(max = 30)
        String phone,

        // Optional address
        @Size(max = 255) String addressLine1,
        @Size(max = 255) String addressLine2,
        @Size(max = 120) String city,
        @Size(max = 2)   String country,     // ISO-3166-1 alpha-2
        @Size(max = 20)  String postalCode,

        // Optional JSON opening hours (schema can be enforced later)
        String openingHoursJson

) {
    // If you want, you can add convenience methods later, e.g. "isEmpty()" to reject no-op updates.
}
