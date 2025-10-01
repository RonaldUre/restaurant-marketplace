package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.command.ChangeCustomerPasswordCommand;

/** Devuelve un mensaje tipo "éxito". */
public interface ChangeCustomerPasswordUseCase {
    String change(ChangeCustomerPasswordCommand cmd);
}
