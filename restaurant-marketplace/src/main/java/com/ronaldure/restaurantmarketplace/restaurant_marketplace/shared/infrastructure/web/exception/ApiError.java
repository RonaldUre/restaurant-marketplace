package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.exception;

/** Standard API error payload shared across modules. */
public record ApiError(
        String code,
        String message,
        long timestamp
) {
    public static ApiError of(String code, String message) {
        return new ApiError(code, message, System.currentTimeMillis());
    }
}
