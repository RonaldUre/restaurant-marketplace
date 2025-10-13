package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security;

import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class ExpiredJwtFilter extends OncePerRequestFilter {

    private final NimbusTokenDecoder nimbusTokenDecoder;

    public ExpiredJwtFilter(NimbusTokenDecoder nimbusTokenDecoder) {
        this.nimbusTokenDecoder = nimbusTokenDecoder;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Solo nos interesa el endpoint de logout
        if (!request.getRequestURI().equals("/auth/logout") || !request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            // ⭐ --- LÓGICA ACTUALIZADA --- ⭐
            // Llamamos a nuestro nuevo método. Este lanzará una excepción si la firma,
            // el issuer, etc., son inválidos, lo cual es bueno.
            // Si el token solo está expirado, nos devolverá los claims.
            JWTClaimsSet claims = nimbusTokenDecoder.extractClaimsEvenIfExpired(token);

            String userId = claims.getSubject();
            if (userId == null) {
                // Si por alguna razón no hay 'sub', no podemos continuar
                filterChain.doFilter(request, response);
                return;
            }

            // Creamos el UserDetails y el Authentication principal, igual que antes
            UserDetails userDetails = User.builder()
                    .username(userId)
                    .password("")
                    .authorities(Collections.emptyList())
                    .build();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (SecurityException e) {
            // Si nuestro método lanzó una excepción (firma inválida, issuer incorrecto,
            // etc.),
            // simplemente dejamos que la cadena de filtros continúe. El siguiente filtro
            // de Spring Security verá que no hay autenticación y devolverá un 401.
            logger.warn("Invalid token on logout path (non-expiration error): " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}