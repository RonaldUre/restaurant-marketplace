package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view;

import java.time.Instant;

/**
 * Card optimizada para panel de plataforma:
 * - Igual que la pública, pero añade createdAt para filtros y orden.
 */
public record PlatformRestaurantCardView(
        Long id,
        String name,
        String slug,
        String status,     // "OPEN" | "CLOSED" | "SUSPENDED"
        String city,
        Instant createdAt
) {}
