package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth.dto.LoginRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth.dto.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/login")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    /** Admin/Platform login: RESTAURANT_ADMIN (with tenantId) or SUPER_ADMIN (without tenantId). */
    @PostMapping("/admin")
    public ResponseEntity<TokenResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        String token = auth.loginAdmin(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new TokenResponse(token, auth.getTtlSeconds()));
    }
}