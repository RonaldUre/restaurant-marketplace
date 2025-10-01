package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.errors.CustomerNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.mapper.CustomerApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.ports.in.GetCustomerMeQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.ports.out.CustomerRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.view.CustomerView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.errors.ForbiddenOperationException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentUserProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCustomerMeHandler implements GetCustomerMeQuery {

    private final CustomerRepository customers;
    private final AccessControl accessControl;
    private final CurrentUserProvider currentUser;

    public GetCustomerMeHandler(CustomerRepository customers,
            AccessControl accessControl,
            CurrentUserProvider currentUser) {
        this.customers = customers;
        this.accessControl = accessControl;
        this.currentUser = currentUser;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerView get() {
        accessControl.requireRole(Roles.CUSTOMER);
        var au = currentUser.requireAuthenticated();

        long me;
        try {
            me = Long.parseLong(au.userId().value());
        } catch (NumberFormatException e) {
            throw new ForbiddenOperationException("Invalid subject for CUSTOMER token");
        }

        var customer = customers.findById(me)
                .orElseThrow(() -> new CustomerNotFoundException(me));

        return CustomerApplicationMapper.toView(customer);
    }
}
