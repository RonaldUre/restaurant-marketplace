package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.errors;

public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException(String email) {
        super("Customer already exists with email: " + email);
    }
}
