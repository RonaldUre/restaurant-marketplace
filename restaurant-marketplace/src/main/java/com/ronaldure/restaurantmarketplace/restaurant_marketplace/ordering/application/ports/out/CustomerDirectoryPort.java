
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;

public interface CustomerDirectoryPort {
    String getCustomerEmail(UserId customerId);
}
