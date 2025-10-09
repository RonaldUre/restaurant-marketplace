package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.errors;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
    public static InsufficientStockException forReserve(Long productId, int requested) {
        return new InsufficientStockException(
                "Insufficient stock to reserve: productId=" + productId + ", requested=" + requested);
    }
    public static InsufficientStockException forConfirm(Long productId, int requested) {
        return new InsufficientStockException(
                "Not enough reserved to confirm: productId=" + productId + ", requested=" + requested);
    }
}
