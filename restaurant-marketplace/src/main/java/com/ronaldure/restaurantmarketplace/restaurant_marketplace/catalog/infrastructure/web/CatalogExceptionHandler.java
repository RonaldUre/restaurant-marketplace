package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.errors.ProductNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.errors.SkuAlreadyInUseException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.exception.ApiError;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Catalog-specific exception handler.
 */
@RestControllerAdvice(basePackages = "com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog")
public class CatalogExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("CATALOG_PRODUCT_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(SkuAlreadyInUseException.class)
    public ResponseEntity<ApiError> handleSkuAlreadyInUse(SkuAlreadyInUseException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("CATALOG_SKU_ALREADY_IN_USE", ex.getMessage()));
    }
}