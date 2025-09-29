package com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.application.errors;

public class NotificationFailedException extends RuntimeException {
    public NotificationFailedException() { super(); }
    public NotificationFailedException(String message) { super(message); }
    public NotificationFailedException(String message, Throwable cause) { super(message, cause); }
}
