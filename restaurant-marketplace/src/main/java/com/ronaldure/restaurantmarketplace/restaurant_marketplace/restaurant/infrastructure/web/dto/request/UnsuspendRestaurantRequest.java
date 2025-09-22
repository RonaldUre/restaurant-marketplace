package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Request para reactivar un restaurante. Exactamente un target: id XOR slug. */
public record UnsuspendRestaurantRequest(

        @Min(1) Long id,

        @Size(min = 1, max = 140)
        @Pattern(regexp = com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.validation.Patterns.SLUG)
        String slug
) {
    public boolean hasExactlyOneTarget() {
        boolean hasId = id != null;
        boolean hasSlug = slug != null && !slug.isBlank();
        return hasId ^ hasSlug;
    }
}
