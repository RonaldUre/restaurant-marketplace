package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors;

public class PaymentDeclinedException extends RuntimeException {
    public PaymentDeclinedException() { super(); }
    public PaymentDeclinedException(String message) { super(message); }
    public PaymentDeclinedException(String message, Throwable cause) { super(message, cause); }
}
