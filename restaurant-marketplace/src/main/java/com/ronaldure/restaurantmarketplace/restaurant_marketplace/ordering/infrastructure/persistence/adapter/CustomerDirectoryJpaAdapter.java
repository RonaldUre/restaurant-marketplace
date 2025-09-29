package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.CustomerDirectoryPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;

public class CustomerDirectoryJpaAdapter implements CustomerDirectoryPort{

    @Override
    public String getCustomerEmail(UserId customerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCustomerEmail'");
    }
    
}
