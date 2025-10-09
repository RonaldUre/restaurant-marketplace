// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/notifications/infrastructure/web/NotificationExceptionHandler.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.application.errors.NotificationFailedException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.exception.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice(basePackages = { 
        "com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications",
        "com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web"
})
public class NotificationExceptionHandler {

    @ExceptionHandler(NotificationFailedException.class)
    public ResponseEntity<ApiError> handleNotificationFailed(NotificationFailedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY) // 502: fallo en sistema externo (SMTP)
                .body(ApiError.of("NOTIFICATION_FAILED", ex.getMessage()));
    }

    // Opcional: inputs inválidos de admin endpoints (si en el futuro los expones)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of("BAD_REQUEST", ex.getMessage()));
    }
}
