package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web.dto.response;

public record CustomerRegisteredResponse(
        Long id,
        String email,
        String name
) { }
