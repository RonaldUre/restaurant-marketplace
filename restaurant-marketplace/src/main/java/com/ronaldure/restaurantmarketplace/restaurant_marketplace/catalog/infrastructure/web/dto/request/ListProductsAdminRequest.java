package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.request;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.Set;

public record ListProductsAdminRequest(
        @Size(max = 200) String q,
        @Size(max = 100) Set<@Size(max = 100) String> categories,
        Boolean published,
        Instant createdFrom,
        Instant createdTo,

        // Pagination
        @Min(0) Integer page,
        @Min(1) Integer size,

        // Sorting
        @Pattern(regexp = "^(createdAt|name|sku|category|priceAmount|published)$")
        String sortBy,
        @Pattern(regexp = "^(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE)
        String sortDir
) {}