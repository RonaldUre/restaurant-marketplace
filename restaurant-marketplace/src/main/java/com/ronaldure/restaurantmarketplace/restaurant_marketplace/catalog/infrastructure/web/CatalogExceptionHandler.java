package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.errors.ProductNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.errors.SkuAlreadyInUseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Catalog-specific exception handler.
 *
 * Responsibilities:
 * - Maps Catalog business exceptions to proper HTTP responses.
 * - Keeps Catalog concerns local, instead of polluting the global handler.
 *
 * Note:
 * - GlobalExceptionHandler (in shared) still handles generic/technical
 * exceptions.
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

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiError.of("CATALOG_INVALID_STATE", ex.getMessage()));
    }

    /**
     * API error response structure.
     * Could be extracted to shared if you want a single consistent class.
     */
    public record ApiError(
            String code,
            String message,
            long timestamp) {
        public static ApiError of(String code, String message) {
            return new ApiError(code, message, System.currentTimeMillis());
        }
    }
}