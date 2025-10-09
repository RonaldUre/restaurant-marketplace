package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors;

public class OrderAlreadyPaidException extends RuntimeException {
    public OrderAlreadyPaidException(Long orderId) {
        super("Order already paid: id=" + orderId);
    }
}
