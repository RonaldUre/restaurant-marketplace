package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.command.RegisterCustomerCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.view.CustomerView;

public interface RegisterCustomerUseCase {
    CustomerView register(RegisterCustomerCommand cmd);
}
