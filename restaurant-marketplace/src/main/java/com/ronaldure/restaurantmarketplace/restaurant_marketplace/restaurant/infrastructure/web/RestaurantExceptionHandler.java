package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.RestaurantNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.SlugAlreadyInUseException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.errors.ForbiddenOperationException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.exception.ApiError;

/**
 * Restaurant-specific exception handler.
 * Ajusta/añade @ExceptionHandler para excepciones de application del módulo
 * (NotFound, conflicts, etc.).
 */
@RestControllerAdvice(basePackages = "com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant")
public class RestaurantExceptionHandler {

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ApiError> handleRestaurantNotFound(RestaurantNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("RESTAURANT_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(SlugAlreadyInUseException.class)
    public ResponseEntity<ApiError> handleSlugAlreadyInUse(SlugAlreadyInUseException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("RESTAURANT_SLUG_ALREADY_IN_USE", ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ApiError> handleForbiddenOperation(ForbiddenOperationException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiError.of("RESTAURANT_FORBIDDEN_OPERATION", ex.getMessage()));
    }
}
