package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;

/** Obtiene datos del cliente (p.ej., email) a partir de su UserId. */
public interface CustomerDirectoryPort {
    String getCustomerEmail(UserId customerId);
}
