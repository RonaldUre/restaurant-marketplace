// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/command/SuspendRestaurantCommand.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Input for SuspendRestaurantUseCase.
 * Exactly one target must be provided: id OR slug.
 * reason is optional (for audit/logging).
 */
public record SuspendRestaurantCommand(

        // Optional ID target (must be positive if present)
        @Min(1)
        Long id,

        // Optional slug target (kebab-case) if id is not provided
        @Size(min = 1, max = 140)
        @Pattern(regexp = com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.validation.Patterns.SLUG)
        String slug,

        // Optional reason for suspension (audit purposes)
        @Size(max = 255)
        String reason

) {
    /**
     * Convenience check to be used by the service implementation.
     * Ensures exactly one of id or slug is provided.
     */
    public boolean hasExactlyOneTarget() {
        boolean hasId = id != null;
        boolean hasSlug = slug != null && !slug.isBlank();
        return hasId ^ hasSlug; // XOR
    }
}