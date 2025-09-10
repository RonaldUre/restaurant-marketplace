package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.TokenDecoder;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.AuthenticatedUser;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.Role;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final TokenDecoder tokenDecoder;

    public JwtAuthenticationFilter(TokenDecoder tokenDecoder) {
        this.tokenDecoder = Objects.requireNonNull(tokenDecoder);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        boolean hasBearer = header != null && header.startsWith("Bearer ");

        try {
            if (!hasBearer) {
                // No token: continue (public routes will be permitAll, others will be rejected by authorization)
                filterChain.doFilter(request, response);
                return;
            }

            String raw = header.substring("Bearer ".length()).trim();
            AuthenticatedUser user = tokenDecoder.decode(raw);

            // Enforce path-specific tenant/role invariants BEFORE building auth
            if (isAdminPath(uri)) {
                // admin requires tenantId
                if (user.tenantId().isEmpty()) {
                    forbidden(response, "Admin route requires tenantId in JWT");
                    return;
                }
                // Set TenantContext from JWT (ignore any request param/header).
                TenantContext.set(user.userId(), parseTenantId(user));
            } else if (isPlatformPath(uri)) {
                // platform requires SUPER_ADMIN and must not set tenant
                if (!user.isSuperAdmin()) {
                    forbidden(response, "SUPER_ADMIN role required");
                    return;
                }
                // Do NOT set tenant in platform
                TenantContext.set(user.userId(), null);
            } else {
                // public or customer routes: do not set tenant
                TenantContext.set(user.userId(), null);
            }

            // Build Spring Authentication with ROLE_ authorities
            Set<SimpleGrantedAuthority> authorities = user.roles().stream()
                    .map(Role::name)
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .collect(Collectors.toSet());

            AbstractAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, authorities);

            org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (SecurityException ex) {
            unauthorized(response, ex.getMessage());
        } finally {
            // Always clear per-request context
            TenantContext.clear();
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    private boolean isAdminPath(String uri) {
        return uri.startsWith("/admin/");
    }

    private boolean isPlatformPath(String uri) {
        return uri.startsWith("/platform/");
    }

    private Long parseTenantId(AuthenticatedUser user) {
        // Our AuthenticatedUser stores TenantId as VO with string value; convert to Long here for TenantContext
        return user.tenantId().map(t -> Long.parseLong(t.value())).orElse(null);
    }

    private void unauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"unauthorized\",\"message\":\"" + sanitize(msg) + "\"}");
    }

    private void forbidden(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"forbidden\",\"message\":\"" + sanitize(msg) + "\"}");
    }

    private String sanitize(String in) {
        return in == null ? "" : in.replace("\"", "'");
    }
}