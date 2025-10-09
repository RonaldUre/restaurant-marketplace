// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/web/OrderExceptionHandler.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.exception.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice(basePackages = "com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering")
public class OrderExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiError> handleOrderNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("ORDER_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(OrderCancellationNotAllowedException.class)
    public ResponseEntity<ApiError> handleCancelNotAllowed(OrderCancellationNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("ORDER_CANCELLATION_NOT_ALLOWED", ex.getMessage()));
    }

    @ExceptionHandler(OrderAlreadyPaidException.class)
    public ResponseEntity<ApiError> handleAlreadyPaid(OrderAlreadyPaidException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("ORDER_ALREADY_PAID", ex.getMessage()));
    }

    @ExceptionHandler(PaymentDeclinedException.class)
    public ResponseEntity<ApiError> handlePaymentDeclined(PaymentDeclinedException ex) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                .body(ApiError.of("PAYMENT_DECLINED", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateIdempotencyKeyException.class)
    public ResponseEntity<ApiError> handleDuplicateIdempotency(DuplicateIdempotencyKeyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("DUPLICATE_IDEMPOTENCY_KEY", ex.getMessage()));
    }
}
