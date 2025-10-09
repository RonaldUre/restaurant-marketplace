package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web.dto.response;

import java.time.Instant;

public record CustomerMeResponse(
        Long id,
        String email,
        String name,
        String phone,       // null cuando no hay teléfono
        Instant createdAt,
        Instant updatedAt
) { }
