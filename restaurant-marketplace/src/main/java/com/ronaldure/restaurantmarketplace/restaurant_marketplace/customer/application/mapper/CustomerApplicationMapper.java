package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.view.CustomerView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.Customer;

/** Mapper de dominio → view (sin password). */
public final class CustomerApplicationMapper {
    private CustomerApplicationMapper() {}

    public static CustomerView toView(Customer c) {
        return new CustomerView(
                c.id() != null ? c.id().value() : null,
                c.email().value(),
                c.name().value(),
                c.phone().isEmpty() ? null : c.phone().value(),
                c.createdAt(),
                c.updatedAt()
        );
    }
}
