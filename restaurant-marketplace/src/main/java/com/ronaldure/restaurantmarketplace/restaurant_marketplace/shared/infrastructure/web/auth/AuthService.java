package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.Role;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.persistence.entity.JpaUserEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.persistence.repository.UserAuthJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security.RefreshTokenService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserAuthJpaRepository users;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokens;
    private final long ttlSeconds; // TTL del access token (en segundos)

    public AuthService(UserAuthJpaRepository users,
                       PasswordEncoder passwordEncoder,
                       RefreshTokenService refreshTokens,
                       @Value("${jwt.accessTokenTtlMinutes:15}") long ttlMinutes) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokens = refreshTokens;
        this.ttlSeconds = ttlMinutes * 60;
    }

    /**
     * Nuevo flujo: devuelve access + refresh y TTL del access.
     * Incluye IP y User-Agent para auditoría de la sesión.
     */
    public LoginResult loginAdmin(String email, String rawPassword, String ip, String userAgent) {
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

        // Emite y persiste refresh; genera access token corto
        var pair = refreshTokens.issueOnLogin(u.getId(), List.of(role), tenantId,
                (ip == null || ip.isBlank()) ? "unknown" : ip,
                (userAgent == null || userAgent.isBlank()) ? "unknown" : userAgent
        );

        return new LoginResult(pair.accessToken(), pair.refreshToken(), ttlSeconds);
    }

    /**
     * Overload temporal para compatibilidad con el controller actual.
     * Devuelve SOLO el access token (como antes). En cuanto actualicemos el controller
     * para responder ambos tokens, elimina este método.
     */
    public String loginAdmin(String email, String rawPassword) {
        var res = loginAdmin(email, rawPassword, "unknown", "unknown");
        return res.accessToken(); // mantiene comportamiento previo (solo access)
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    /** Resultado de login: tokens y TTL del access. */
    public record LoginResult(String accessToken, String refreshToken, long accessTtlSeconds) {}
}
