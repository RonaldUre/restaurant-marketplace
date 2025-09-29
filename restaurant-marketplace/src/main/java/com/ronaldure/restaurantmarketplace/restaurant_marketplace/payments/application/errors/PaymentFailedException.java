package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.application.errors;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException() { super(); }
    public PaymentFailedException(String message) { super(message); }
    public PaymentFailedException(String message, Throwable cause) { super(message, cause); }
}
