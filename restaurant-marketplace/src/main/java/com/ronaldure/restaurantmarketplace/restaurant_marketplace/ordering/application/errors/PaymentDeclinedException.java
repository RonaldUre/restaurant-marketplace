package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors;

public class PaymentDeclinedException extends RuntimeException {
    public PaymentDeclinedException(String reason) {
        super("Payment declined: " + reason);
    }
    public PaymentDeclinedException(Long orderId, String reason) {
        super("Payment declined for order " + orderId + ": " + reason);
    }
}
