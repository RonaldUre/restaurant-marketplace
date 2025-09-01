// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/shared/application/query/PageRequest.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query;

/**
 * Generic pagination request for queries.
 */
public record PageRequest(
        int page,
        int size
) {}
