package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.projection;

import java.time.Instant;

/** Proyección mínima para listado de plataforma (incluye createdAt). */
public interface PlatformRestaurantCardProjection {
    Long getId();
    String getName();
    String getSlug();
    String getStatus();   // "OPEN" | "CLOSED" | "SUSPENDED"
    String getCity();
    Instant getCreatedAt();
}
