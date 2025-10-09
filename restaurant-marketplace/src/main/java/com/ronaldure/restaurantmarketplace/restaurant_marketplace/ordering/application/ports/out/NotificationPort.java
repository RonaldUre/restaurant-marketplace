package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out;

/** Envío de notificaciones al cliente. Implementado en `notifications`. */
public interface NotificationPort {
    void sendOrderConfirmed(String toEmail, long orderId, String restaurantName, String summary);
    void sendOrderCancelled(String toEmail, long orderId, String reason);
    void sendPaymentFailed(String toEmail, long orderId, String reason);
}
