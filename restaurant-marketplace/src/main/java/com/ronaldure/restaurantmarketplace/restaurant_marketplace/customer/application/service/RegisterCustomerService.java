// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/customer/application/service/RegisterCustomerService.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.command.RegisterCustomerCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.errors.CustomerAlreadyExistsException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.mapper.CustomerApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.ports.in.RegisterCustomerUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.ports.out.CustomerRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.view.CustomerView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.factory.CustomerFactory;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.Customer;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo.Email;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterCustomerService implements RegisterCustomerUseCase {

    private final CustomerRepository customers;
    private final PasswordEncoder passwordEncoder;

    public RegisterCustomerService(CustomerRepository customers, PasswordEncoder passwordEncoder) {
        this.customers = customers;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public CustomerView register(RegisterCustomerCommand cmd) {
        if (customers.existsByEmail(Email.of(cmd.email()))) {
            throw new CustomerAlreadyExistsException(cmd.email());
        }

        String hash = passwordEncoder.encode(cmd.password());
        Customer aggregate = CustomerFactory.newFrom(cmd, hash);
        Customer saved = customers.save(aggregate);

        return CustomerApplicationMapper.toView(saved);
    }
}
