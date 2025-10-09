package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.view.CustomerView;

public interface GetCustomerMeQuery {
    CustomerView get();
}
