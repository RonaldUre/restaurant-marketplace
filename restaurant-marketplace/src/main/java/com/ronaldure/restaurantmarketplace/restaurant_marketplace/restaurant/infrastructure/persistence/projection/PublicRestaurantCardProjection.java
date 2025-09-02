package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.projection;

/** Minimal projection for public listing (cards). JPA interface-based projection. */
public interface PublicRestaurantCardProjection {
    Long getId();
    String getName();
    String getSlug();
    String getStatus(); // "OPEN" | "CLOSED" (en público filtramos OPEN)
    String getCity();
}