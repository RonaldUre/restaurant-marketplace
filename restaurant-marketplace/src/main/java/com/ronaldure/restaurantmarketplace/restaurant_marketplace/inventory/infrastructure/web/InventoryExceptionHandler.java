// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/inventory/infrastructure/web/InventoryExceptionHandler.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.errors.InsufficientStockException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.errors.InventoryItemNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.errors.InventoryOperationNotAllowedException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.exception.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Inventory-specific exception handler.
 */
@RestControllerAdvice(basePackages = {
  "com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory",
  "com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web"
})
public class InventoryExceptionHandler {

    @ExceptionHandler(InventoryItemNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(InventoryItemNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("INVENTORY_ITEM_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiError> handleInsufficient(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("INVENTORY_INSUFFICIENT_STOCK", ex.getMessage()));
    }

    @ExceptionHandler(InventoryOperationNotAllowedException.class)
    public ResponseEntity<ApiError> handleOpNotAllowed(InventoryOperationNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("INVENTORY_OPERATION_NOT_ALLOWED", ex.getMessage()));
    }

}
