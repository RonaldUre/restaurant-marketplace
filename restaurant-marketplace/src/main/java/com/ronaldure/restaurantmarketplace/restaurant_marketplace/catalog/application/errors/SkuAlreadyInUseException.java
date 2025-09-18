// .../application/errors/SkuAlreadyInUseException.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.errors;

public class SkuAlreadyInUseException extends RuntimeException {
    public SkuAlreadyInUseException(String sku) {
        super("SKU already in use: " + sku);
    }
}
