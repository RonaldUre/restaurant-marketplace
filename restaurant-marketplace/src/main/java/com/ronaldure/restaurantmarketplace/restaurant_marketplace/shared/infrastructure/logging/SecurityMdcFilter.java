package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.logging;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentUserProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.AuthenticatedUser;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security.TenantContext;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Enrich logs with userId, tenantId, and roles, if available.
 * Assumes another component (future JWT filter) sets TenantContext and provides CurrentUserProvider.
 */
public class SecurityMdcFilter extends OncePerRequestFilter {

    private final CurrentUserProvider currentUserProvider;

    public SecurityMdcFilter(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            currentUserProvider.findAuthenticated().ifPresent(this::putUser);
            TenantContext.getTenantId().ifPresent(t -> MDC.put("tenantId", t.toString()));
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("userId");
            MDC.remove("tenantId");
            MDC.remove("roles");
        }
    }

    private void putUser(AuthenticatedUser user) {
        MDC.put("userId", user.userId().value());
        String roles = user.roles().stream().map(Enum::name).collect(Collectors.joining(","));
        MDC.put("roles", roles);
    }
}