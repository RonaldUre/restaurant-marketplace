package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.projection;

/** Minimal projection for platform select: id + name. */
public interface PlatformRestaurantSelectProjection {
    Long getId();
    String getName();
}
