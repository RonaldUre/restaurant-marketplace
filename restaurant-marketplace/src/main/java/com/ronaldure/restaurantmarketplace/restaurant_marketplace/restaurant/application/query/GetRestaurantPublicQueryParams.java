// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/query/GetRestaurantPublicQueryParams.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Parameters for fetching a single restaurant in public context.
 * Either id or slug must be provided.
 */
public record GetRestaurantPublicQueryParams(

        @Min(1)
        Long id,

        @Size(min = 1, max = 140)
        @Pattern(regexp = com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.validation.Patterns.SLUG)
        String slug

) {
    public boolean hasExactlyOneTarget() {
        boolean hasId = id != null;
        boolean hasSlug = slug != null && !slug.isBlank();
        return hasId ^ hasSlug; // XOR
    }
}
