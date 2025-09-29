package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors;

public class OrderCancellationNotAllowedException extends RuntimeException {
    public OrderCancellationNotAllowedException() { super(); }
    public OrderCancellationNotAllowedException(String message) { super(message); }
    public OrderCancellationNotAllowedException(String message, Throwable cause) { super(message, cause); }
}
