package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view;

/**
 * Lightweight view for public restaurant listings (cards).
 * Optimized for browsing: minimal fields, no private info.
 */
public record RestaurantCardView(
        Long id,
        String name,
        String slug,
        String status,   // "OPEN" | "CLOSED"
        String city      // optional: can be null if not set
) {}
