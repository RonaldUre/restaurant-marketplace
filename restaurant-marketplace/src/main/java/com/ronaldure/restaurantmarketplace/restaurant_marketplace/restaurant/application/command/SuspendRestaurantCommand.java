// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/command/SuspendRestaurantCommand.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command;


/**
 * Input for SuspendRestaurantUseCase.
 * Exactly one target must be provided: id OR slug.
 * reason is optional (for audit/logging).
 */
public record SuspendRestaurantCommand(

        // Optional ID target (must be positive if present)
        Long id,

        // Optional slug target (kebab-case) if id is not provided
        String slug,

        // Optional reason for suspension (audit purposes)
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
