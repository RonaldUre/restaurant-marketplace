package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto;

public record RestaurantCardResponse(
        Long id,
        String name,
        String slug,
        String status,
        String city
) {}