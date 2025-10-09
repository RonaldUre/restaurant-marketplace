package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in;

public interface CheckSlugAvailabilityQuery {
    Result check(String candidate);

    record Result(String value, String normalized, boolean available) {}
}
