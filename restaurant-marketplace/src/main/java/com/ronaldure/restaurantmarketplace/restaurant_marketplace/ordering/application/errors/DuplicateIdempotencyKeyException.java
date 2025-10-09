package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors;

public class DuplicateIdempotencyKeyException extends RuntimeException {
    public DuplicateIdempotencyKeyException(String key) {
        super("Idempotency key already used: " + key);
    }
}
