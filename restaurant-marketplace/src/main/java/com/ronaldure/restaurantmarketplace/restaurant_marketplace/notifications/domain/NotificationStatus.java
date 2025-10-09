// src/main/java/.../notifications/domain/NotificationStatus.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.domain;

/**
 * Delivery state for a notification attempt/log.
 * Persist with @Enumerated(EnumType.STRING).
 */
public enum NotificationStatus {
    PENDING,
    SENT,
    FAILED
}
