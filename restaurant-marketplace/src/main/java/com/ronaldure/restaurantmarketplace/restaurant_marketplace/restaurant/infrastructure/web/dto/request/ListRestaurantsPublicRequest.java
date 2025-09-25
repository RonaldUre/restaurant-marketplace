package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/** Request para listado público con paginación y filtro opcional por ciudad. */
public record ListRestaurantsPublicRequest(
        @Min(0) Integer page,
        @Min(1) Integer size,
        @Size(min = 1, max = 120) String city
) {}
