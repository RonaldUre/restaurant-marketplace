package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.AuthenticatedUser;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security.RefreshTokenService;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth.dto.LoginRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth.dto.RefreshRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth.dto.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

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
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshRequest request,
            @RequestParam(name = "all", defaultValue = "false") boolean all,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // --- LÓGICA DE EXTRACCIÓN DE ID MEJORADA ---
        Object principal = authentication.getPrincipal();
        String userIdFromAccessToken;

        if (principal instanceof AuthenticatedUser authenticatedUser) {
            // CASO 1: Token activo, tenemos nuestro objeto de usuario personalizado.
            // Obtenemos el ID directamente de él.
            userIdFromAccessToken = String.valueOf(authenticatedUser.userId());
        } else if (principal instanceof UserDetails userDetails) {
            // CASO 2: Token vencido, tenemos el objeto User genérico del ExpiredJwtFilter.
            // Su "username" fue configurado para ser el ID.
            userIdFromAccessToken = userDetails.getUsername();
        } else {
            // CASO DE RESPALDO (no debería ocurrir, pero es una buena práctica)
            throw new IllegalStateException("Authentication principal is of an unknown type: " + principal.getClass());
        }

        // Ahora, userIdFromAccessToken siempre será un string numérico ("4")
        refreshTokens.logout(request.getRefreshToken(), userIdFromAccessToken, all);

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
