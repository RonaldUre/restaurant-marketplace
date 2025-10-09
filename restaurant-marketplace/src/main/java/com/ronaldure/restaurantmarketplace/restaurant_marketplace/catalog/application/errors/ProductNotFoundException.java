package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.errors;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product not found: id=" + id);
    }
}
