// src/main/java/.../notifications/domain/NotificationType.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.domain;

/**
 * Business event that triggers a notification.
 * Kept stable for reporting and filtering.
 */
public enum NotificationType {
    ORDER_CONFIRMED,
    ORDER_CANCELLED,
    PAYMENT_FAILED
}
