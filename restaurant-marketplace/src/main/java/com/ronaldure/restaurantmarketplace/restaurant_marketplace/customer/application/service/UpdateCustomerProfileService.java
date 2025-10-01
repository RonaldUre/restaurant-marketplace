// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/customer/application/service/UpdateCustomerProfileService.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.command.UpdateCustomerProfileCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.factory.CustomerFactory;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.mapper.CustomerApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.ports.in.UpdateCustomerProfileUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.ports.out.CustomerRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.view.CustomerView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.Customer;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.errors.CustomerNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.errors.ForbiddenOperationException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentUserProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCustomerProfileService implements UpdateCustomerProfileUseCase {

    private final CustomerRepository customers;
    private final AccessControl accessControl;
    private final CurrentUserProvider currentUser;

    public UpdateCustomerProfileService(CustomerRepository customers,
                                        AccessControl accessControl,
                                        CurrentUserProvider currentUser) {
        this.customers = customers;
        this.accessControl = accessControl;
        this.currentUser = currentUser;
    }

    @Override
    @Transactional
    public CustomerView update(UpdateCustomerProfileCommand cmd) {
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

        var payload = CustomerFactory.toUpdatePayload(cmd);
        var newName  = payload.name()  != null ? payload.name()  : customer.name();
        var newPhone = payload.phone() != null ? payload.phone() : customer.phone();

        customer.updateProfile(newName, newPhone);
        Customer saved = customers.save(customer);

        return CustomerApplicationMapper.toView(saved);
    }
}
