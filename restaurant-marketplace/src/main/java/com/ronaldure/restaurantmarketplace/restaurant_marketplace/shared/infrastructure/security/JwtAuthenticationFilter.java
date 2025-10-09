package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.TokenDecoder;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.AuthenticatedUser;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.Role;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
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
        final String uri = request.getRequestURI();
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        final boolean hasBearer = header != null && header.startsWith("Bearer ");

        try {
            if (!hasBearer) {
                // sin token: continúa (permitAll/deny lo decidirá la capa de autorización)
                filterChain.doFilter(request, response);
                return;
            }

            final String raw = header.substring("Bearer ".length()).trim();
            final AuthenticatedUser user = tokenDecoder.decode(raw);

            // Enforce invariants per path
            if (isAdminPath(uri)) {
                if (user.tenantId().isEmpty()) {
                    forbidden(response, "Admin route requires tenantId in JWT");
                    return;
                }
                Long tenantId = user.tenantId().map(TenantId::value)
                        .orElseThrow(() -> new SecurityException("Invalid tenantId"));
                TenantContext.set(user.userId(), tenantId);

            } else if (isPlatformPath(uri)) {
                if (!user.isSuperAdmin()) {
                    forbidden(response, "SUPER_ADMIN role required");
                    return;
                }
                TenantContext.set(user.userId(), null);
            } else {
                // público/customer: no setear tenant
                TenantContext.set(user.userId(), null);
            }

            // Construye Authentication con authorities ROLE_*
            Set<SimpleGrantedAuthority> authorities = user.roles().stream()
                    .map(Role::name)
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .collect(Collectors.toSet());

            AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
                    authorities);

            org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (SecurityException se) {
            // Cualquier problema de firma/claims/exp/tenantId inválido ⇒ 401
            unauthorized(response, se.getMessage());
            return;
        } catch (RuntimeException re) {
            // Parseos fuera del decoder, etc. ⇒ 401 genérico
            unauthorized(response, "Invalid token");
            return;
        } finally {
            // Limpia SIEMPRE el contexto de tenant por request
            TenantContext.clear();
            // Importante: NO limpiar SecurityContext aquí; lo maneja Spring Security.
        }
    }

    private boolean isAdminPath(String uri) {
        return "/admin".equals(uri) || uri.startsWith("/admin/");
    }

    private boolean isPlatformPath(String uri) {
        return "/platform".equals(uri) || uri.startsWith("/platform/");
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
