package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.application.errors;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String reason) {
        super("Payment failed: " + reason);
    }
    public PaymentFailedException(Long orderId, String reason) {
        super("Payment failed for order " + orderId + ": " + reason);
    }
}
