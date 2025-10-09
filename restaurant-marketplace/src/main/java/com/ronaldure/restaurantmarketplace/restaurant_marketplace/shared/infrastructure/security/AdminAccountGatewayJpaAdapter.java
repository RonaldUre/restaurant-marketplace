package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.AdminAccountGateway;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.persistence.entity.JpaUserEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.persistence.repository.UserAuthJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountGatewayJpaAdapter implements AdminAccountGateway {

    private static final String ROLE_RESTAURANT_ADMIN = "RESTAURANT_ADMIN";

    private final UserAuthJpaRepository users;
    private final PasswordEncoder passwordEncoder;

    public AdminAccountGatewayJpaAdapter(UserAuthJpaRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createTenantAdmin(Long tenantId, String email, String rawPassword) {
        users.findByEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("Admin email already exists");
        });

        JpaUserEntity u = new JpaUserEntity();
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setRole(ROLE_RESTAURANT_ADMIN);
        u.setTenantId(tenantId);
        users.save(u);
    }
}
