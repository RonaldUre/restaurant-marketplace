package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.errors;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}
