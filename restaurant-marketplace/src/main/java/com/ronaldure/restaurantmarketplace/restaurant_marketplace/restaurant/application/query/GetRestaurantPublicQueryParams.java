// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/query/GetRestaurantPublicQueryParams.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query;


/**
 * Parameters for fetching a single restaurant in public context.
 * Either id or slug must be provided.
 */
public record GetRestaurantPublicQueryParams(

        Long id,

        String slug

) {
    public boolean hasExactlyOneTarget() {
        boolean hasId = id != null;
        boolean hasSlug = slug != null && !slug.isBlank();
        return hasId ^ hasSlug; // XOR
    }
}
