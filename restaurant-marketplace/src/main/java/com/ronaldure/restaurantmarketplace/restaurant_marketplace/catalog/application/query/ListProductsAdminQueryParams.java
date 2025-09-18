// .../application/query/ListProductsAdminQueryParams.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query;

import java.time.Instant;
import java.util.Set;

public record ListProductsAdminQueryParams(
        String q,                 // search by name/sku
        Set<String> categories,   // multi-filter
        Boolean published,        // null = all
        Instant createdFrom,
        Instant createdTo,
        String sort               // e.g. "createdAt,desc" | "name,asc"
) { }
