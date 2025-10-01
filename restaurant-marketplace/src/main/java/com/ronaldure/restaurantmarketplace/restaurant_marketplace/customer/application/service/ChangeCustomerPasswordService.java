// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/customer/application/service/ChangeCustomerPasswordService.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.command.ChangeCustomerPasswordCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.errors.CustomerNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.errors.InvalidCredentialsException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.ports.in.ChangeCustomerPasswordUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.ports.out.CustomerRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.errors.ForbiddenOperationException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentUserProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeCustomerPasswordService implements ChangeCustomerPasswordUseCase {

    private final CustomerRepository customers;
    private final PasswordEncoder passwordEncoder;
    private final AccessControl accessControl;
    private final CurrentUserProvider currentUser;

    public ChangeCustomerPasswordService(CustomerRepository customers,
            PasswordEncoder passwordEncoder,
            AccessControl accessControl,
            CurrentUserProvider currentUser) {
        this.customers = customers;
        this.passwordEncoder = passwordEncoder;
        this.accessControl = accessControl;
        this.currentUser = currentUser;
    }

    @Override
    @Transactional
    public String change(ChangeCustomerPasswordCommand cmd) {
        accessControl.requireRole(Roles.CUSTOMER);

        var au = currentUser.requireAuthenticated();
        final long me;
        try {
            me = Long.parseLong(au.userId().value());
        } catch (NumberFormatException e) {
            throw new ForbiddenOperationException("Invalid subject for CUSTOMER token");
        }

        var customer = customers.findById(me)
                .orElseThrow(() -> new CustomerNotFoundException(me));

        // Verifica contraseña actual
        if (!passwordEncoder.matches(cmd.currentPassword(), customer.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        // (Opcional) Política mínima de password antes de hashear
        // if (cmd.newPassword() == null || cmd.newPassword().length() < 8) {
        // throw new IllegalArgumentException("New password must be at least 8
        // characters");
        // }

        String newHash = passwordEncoder.encode(cmd.newPassword());
        customer.changePasswordHash(newHash);

        // Persistimos y aseguramos estado final
        customers.save(customer);

        // (Opcional futuro) revocar sesiones del CUSTOMER tras cambio de password

        return "success";
    }
}
