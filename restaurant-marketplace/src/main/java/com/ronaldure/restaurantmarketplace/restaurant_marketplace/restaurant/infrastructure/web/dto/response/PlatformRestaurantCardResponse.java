package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.response;

import java.time.Instant;

/** DTO para listado de plataforma */
public record PlatformRestaurantCardResponse(
        Long id,
        String name,
        String slug,
        String status,
        String city,
        Instant createdAt
) {}
