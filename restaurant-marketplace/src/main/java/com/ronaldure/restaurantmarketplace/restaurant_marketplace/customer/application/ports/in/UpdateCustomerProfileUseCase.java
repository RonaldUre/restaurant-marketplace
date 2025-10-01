package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.command.UpdateCustomerProfileCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.view.CustomerView;

public interface UpdateCustomerProfileUseCase {
    CustomerView update(UpdateCustomerProfileCommand cmd);
}
