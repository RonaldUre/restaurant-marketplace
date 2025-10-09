package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security.RefreshTokenService;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth.dto.LoginRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth.dto.RefreshRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth.dto.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService auth;
    private final RefreshTokenService refreshTokens;

    public AuthController(AuthService auth, RefreshTokenService refreshTokens) {
        this.auth = auth;
        this.refreshTokens = refreshTokens;
    }

    /**
     * Admin/Platform login: RESTAURANT_ADMIN (con tenantId) o SUPER_ADMIN (sin
     * tenantId).
     */
    @PostMapping("/login/admin")
    public ResponseEntity<TokenResponse> adminLogin(@Valid @RequestBody LoginRequest request,
            HttpServletRequest http) {
        String ip = clientIp(http);
        String ua = userAgent(http);

        var res = auth.loginAdmin(request.getEmail(), request.getPassword(), ip, ua);
        return ResponseEntity.ok(new TokenResponse(
                res.accessToken(),
                res.accessTtlSeconds(),
                res.refreshToken()));
    }

    /**
     * Intercambia un refresh token válido por un nuevo access + refresh (rotación
     * obligatoria).
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request,
            HttpServletRequest http) {
        String ip = clientIp(http);
        String ua = userAgent(http);

        var pair = refreshTokens.rotate(request.getRefreshToken(), ip, ua);
        // TTL del access no cambia aquí; usamos el mismo configurado en AuthService
        long accessTtl = auth.getTtlSeconds();

        return ResponseEntity.ok(new TokenResponse(
                pair.accessToken(),
                accessTtl,
                pair.refreshToken()));
    }

    /**
     * Logout por refresh token. Revoca el RT suministrado.
     * Si ?all=true, revoca todas las sesiones del usuario.
     * Respuesta: 204 No Content (idempotente).
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request,
            @RequestParam(name = "all", defaultValue = "false") boolean all) {
        refreshTokens.logout(request.getRefreshToken(), all);
        return ResponseEntity.noContent().build();
    }

    // -------- helpers ----------
    private String clientIp(HttpServletRequest http) {
        String h = http.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) {
            int comma = h.indexOf(',');
            return comma > 0 ? h.substring(0, comma).trim() : h.trim();
        }
        return http.getRemoteAddr();
    }

    private String userAgent(HttpServletRequest http) {
        String ua = http.getHeader("User-Agent");
        return (ua == null || ua.isBlank()) ? "unknown" : ua;
    }
}
