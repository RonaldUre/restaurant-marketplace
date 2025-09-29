package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors;

public class DuplicateIdempotencyKeyException extends RuntimeException {
    public DuplicateIdempotencyKeyException() { super(); }
    public DuplicateIdempotencyKeyException(String message) { super(message); }
    public DuplicateIdempotencyKeyException(String message, Throwable cause) { super(message, cause); }
}
