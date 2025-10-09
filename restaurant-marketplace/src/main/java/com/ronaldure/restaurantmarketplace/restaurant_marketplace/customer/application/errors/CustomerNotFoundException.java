package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.errors;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long id) {
        super("Customer not found: id=" + id);
    }
    public static CustomerNotFoundException byEmail(String email) {
        return new CustomerNotFoundException(-1L) {
            @Override public String getMessage() { return "Customer not found: email=" + email; }
        };
    }
}
