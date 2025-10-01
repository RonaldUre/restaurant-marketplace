// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/customer/infrastructure/web/CustomerExceptionHandler.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.errors.CustomerAlreadyExistsException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.errors.CustomerNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.errors.InvalidCredentialsException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.exception.ApiError;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Handler de excepciones específico del módulo Customer. */
@RestControllerAdvice(basePackages = "com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer")
public class CustomerExceptionHandler {

    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleCustomerAlreadyExists(CustomerAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("CUSTOMER_ALREADY_EXISTS", ex.getMessage()));
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiError> handleCustomerNotFound(CustomerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("CUSTOMER_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.of("INVALID_CREDENTIALS", ex.getMessage()));
    }
}
