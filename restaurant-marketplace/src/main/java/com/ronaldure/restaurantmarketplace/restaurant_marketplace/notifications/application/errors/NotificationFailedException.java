package com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.application.errors;

public class NotificationFailedException extends RuntimeException {
    public NotificationFailedException(String message) {
        super("Notification failed: " + message);
    }
    public NotificationFailedException(Long orderId, String message) {
        super("Notification failed for order " + orderId + ": " + message);
    }
}
