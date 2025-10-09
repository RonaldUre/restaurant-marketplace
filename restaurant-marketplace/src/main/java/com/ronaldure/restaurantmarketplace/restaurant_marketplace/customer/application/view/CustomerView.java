package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.view;

import java.time.Instant;

/** Read model expuesto hacia arriba (sin contraseña). */
public record CustomerView(
        Long id,
        String email,
        String name,
        String phone,
        Instant createdAt,
        Instant updatedAt
) { }
