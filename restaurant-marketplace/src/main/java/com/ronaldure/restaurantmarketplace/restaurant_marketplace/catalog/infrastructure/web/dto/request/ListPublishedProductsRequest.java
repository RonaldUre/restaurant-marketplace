package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.request;

import jakarta.validation.constraints.*;

public record ListPublishedProductsRequest(
        @Size(max = 200) String q,
        @Size(max = 100) String category,

        // Pagination
        @Min(0) Integer page,
        @Min(1) Integer size,

        // Sorting
        @Pattern(regexp = "^(name|priceAmount)$")
        String sortBy,
        @Pattern(regexp = "^(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE)
        String sortDir
) {}