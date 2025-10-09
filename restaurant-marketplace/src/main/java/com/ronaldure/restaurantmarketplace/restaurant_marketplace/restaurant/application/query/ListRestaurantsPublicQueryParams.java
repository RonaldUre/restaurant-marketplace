// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/query/ListRestaurantsPublicQueryParams.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query;

/**
 * Parameters for public restaurant listing.
 * This allows future extension: filter by city, category, etc.
 */
public record ListRestaurantsPublicQueryParams(
        int page,
        int size,
        String cityFilter // optional, may be null
) {}
