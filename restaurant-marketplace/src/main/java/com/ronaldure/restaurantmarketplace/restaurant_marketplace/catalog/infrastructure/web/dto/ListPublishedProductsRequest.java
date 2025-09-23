package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto;

import jakarta.validation.constraints.*;

public record ListPublishedProductsRequest(
    @Size(max = 200) String q,
    @Size(max = 100) String category,
    @Pattern(regexp = "^(name|priceAmount),(asc|desc)$")
    String sort
) {}