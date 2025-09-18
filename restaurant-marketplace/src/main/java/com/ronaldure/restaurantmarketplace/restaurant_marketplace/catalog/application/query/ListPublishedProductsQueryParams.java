// .../application/query/ListPublishedProductsQueryParams.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query;

public record ListPublishedProductsQueryParams(
        Long restaurantId,
        String q,
        String category,
        String sort               // e.g. "name,asc" | "priceAmount,asc|desc"
) { }
