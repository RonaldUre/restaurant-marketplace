package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors;

public class OrderCancellationNotAllowedException extends RuntimeException {
    public OrderCancellationNotAllowedException(Long orderId) {
        super("Order cancellation not allowed in current state: id=" + orderId);
    }
}
