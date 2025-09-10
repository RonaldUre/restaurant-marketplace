package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.Role;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.persistence.entity.JpaUserEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.persistence.repository.UserAuthJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security.JwtTokenGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserAuthJpaRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator tokenGenerator;
    private final long ttlSeconds;

    public AuthService(UserAuthJpaRepository users,
                       PasswordEncoder passwordEncoder,
                       JwtTokenGenerator tokenGenerator,
                       @Value("${jwt.accessTokenTtlMinutes:15}") long ttlMinutes) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.ttlSeconds = ttlMinutes * 60;
    }

    public String loginAdmin(String email, String rawPassword) {
        JpaUserEntity u = users.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, u.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        Role role = Role.valueOf(u.getRole());
        if (role != Role.RESTAURANT_ADMIN && role != Role.SUPER_ADMIN) {
            throw new IllegalStateException("Unsupported role for this login flow: " + role);
        }

        Long tenantId = (role == Role.RESTAURANT_ADMIN) ? u.getTenantId() : null;
        return tokenGenerator.generate(String.valueOf(u.getId()), List.of(role), tenantId);
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }
}