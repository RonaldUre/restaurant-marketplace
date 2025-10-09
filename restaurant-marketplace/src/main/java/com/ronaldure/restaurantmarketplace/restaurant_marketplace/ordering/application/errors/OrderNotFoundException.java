package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("Order not found: id=" + id);
    }
}
