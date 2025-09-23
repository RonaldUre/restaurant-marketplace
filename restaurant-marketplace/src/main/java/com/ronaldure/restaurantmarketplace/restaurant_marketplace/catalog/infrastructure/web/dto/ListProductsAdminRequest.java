package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.Set;

public record ListProductsAdminRequest(
    @Size(max = 200) String q,
    @Size(max = 100) Set<@Size(max=100) String> categories,
    Boolean published,
    Instant createdFrom,
    Instant createdTo,
    @Pattern(regexp="^(createdAt|name|sku|category|priceAmount|published),(asc|desc)$")
    String sort
) {}