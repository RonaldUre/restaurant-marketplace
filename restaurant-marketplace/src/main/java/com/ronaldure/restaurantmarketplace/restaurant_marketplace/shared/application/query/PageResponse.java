// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/shared/application/query/PageResponse.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query;

import java.util.List;

/**
 * Generic pagination response wrapper.
 */
public record PageResponse<T>(
        List<T> items,
        long totalElements,
        int totalPages
) {}
