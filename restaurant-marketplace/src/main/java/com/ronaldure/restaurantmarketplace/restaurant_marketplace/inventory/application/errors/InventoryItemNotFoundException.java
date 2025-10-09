// src/main/java/.../inventory/application/errors/InventoryItemNotFoundException.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.errors;

public class InventoryItemNotFoundException extends RuntimeException {
    public InventoryItemNotFoundException(Long productId) {
        super("Inventory item not found for productId=" + productId);
    }
}
