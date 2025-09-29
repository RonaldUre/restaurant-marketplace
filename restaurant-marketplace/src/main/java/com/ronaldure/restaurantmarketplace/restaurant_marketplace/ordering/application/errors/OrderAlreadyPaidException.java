package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors;

public class OrderAlreadyPaidException extends RuntimeException {
    public OrderAlreadyPaidException() { super(); }
    public OrderAlreadyPaidException(String message) { super(message); }
    public OrderAlreadyPaidException(String message, Throwable cause) { super(message, cause); }
}
