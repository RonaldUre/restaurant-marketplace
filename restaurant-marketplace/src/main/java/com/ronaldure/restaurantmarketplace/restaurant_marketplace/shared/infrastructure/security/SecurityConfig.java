package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentUserProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.TokenDecoder;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.logging.SecurityMdcFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(TokenDecoder tokenDecoder) {
        return new JwtAuthenticationFilter(tokenDecoder);
    }

    @Bean
    public ExpiredJwtFilter expiredJwtFilter(NimbusTokenDecoder nimbusTokenDecoder) {
        return new ExpiredJwtFilter(nimbusTokenDecoder);
    }

    @Bean
    public SecurityMdcFilter securityMdcFilter(CurrentUserProvider currentUserProvider) {
        return new SecurityMdcFilter(currentUserProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            ExpiredJwtFilter expiredJwtFilter,
            SecurityMdcFilter securityMdcFilter,
            AuthenticationEntryPoint authEntryPoint,
            AccessDeniedHandler accessDeniedHandler) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Login endpoints
                        .requestMatchers("/auth/login/**").permitAll()
                        .requestMatchers("/auth/refresh").permitAll()
                        .requestMatchers("/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/public/customers").permitAll()
                        // Public read
                        .requestMatchers(HttpMethod.GET, "/public/restaurants/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/restaurants/**").permitAll()
                        // Orders for customers
                        .requestMatchers(HttpMethod.POST, "/orders").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/orders/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/customers/me").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/customers/me").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/customers/password").hasRole("CUSTOMER")
                        // Admin tenant routes
                        .requestMatchers("/admin/**").hasRole("RESTAURANT_ADMIN")
                        // Platform routes
                        .requestMatchers("/platform/**").hasRole("SUPER_ADMIN")
                        // OpenAPI / Actuator (adjust as needed)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/actuator/health").permitAll()
                        // Everything else requires auth
                        .anyRequest().authenticated());

        // Order: JWT first, then MDC after authentication is set
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(expiredJwtFilter, JwtAuthenticationFilter.class);
        http.addFilterAfter(securityMdcFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    // CORS config
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource(
            @Value("${security.cors.allowed-origins}") String allowedOrigins,
            @Value("${security.cors.allowed-methods}") String allowedMethods,
            @Value("${security.cors.allowed-headers}") String allowedHeaders) {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        cfg.setAllowedMethods(List.of(allowedMethods.split(",")));
        cfg.setAllowedHeaders(List.of(allowedHeaders.split(",")));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
