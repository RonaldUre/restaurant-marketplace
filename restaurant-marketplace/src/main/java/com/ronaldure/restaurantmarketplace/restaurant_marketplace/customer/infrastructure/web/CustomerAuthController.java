// src/main/java/.../customer/infrastructure/web/CustomerAuthController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth.dto.LoginRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth.dto.TokenResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/auth")
public class CustomerAuthController {

    private final AuthService auth;

    public CustomerAuthController(AuthService auth) {
        this.auth = auth;
    }

    /** Login exclusivo para CUSTOMER. */
    @PostMapping("/login/customer")
    public ResponseEntity<TokenResponse> customerLogin(@Valid @RequestBody LoginRequest request,
                                                       HttpServletRequest http) {
        String ip = clientIp(http);
        String ua = userAgent(http);

        var res = auth.loginCustomer(request.getEmail(), request.getPassword(), ip, ua);
        return ResponseEntity.ok(new TokenResponse(
                res.accessToken(),
                res.accessTtlSeconds(),
                res.refreshToken()
        ));
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
