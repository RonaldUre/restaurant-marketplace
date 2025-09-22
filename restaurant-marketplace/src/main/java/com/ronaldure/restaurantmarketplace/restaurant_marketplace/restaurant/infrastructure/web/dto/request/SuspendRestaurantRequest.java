package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request para suspender un restaurante. Debe venir exactamente UNO de:
 * - id (path/param)  O
 * - slug (path/param)
 * 'reason' es opcional.
 */
public record SuspendRestaurantRequest(

        @Min(1) Long id,

        @Size(min = 1, max = 140)
        @Pattern(regexp = com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.validation.Patterns.SLUG)
        String slug,

        @Size(max = 255) String reason
) {
    public boolean hasExactlyOneTarget() {
        boolean hasId = id != null;
        boolean hasSlug = slug != null && !slug.isBlank();
        return hasId ^ hasSlug;
    }
}
