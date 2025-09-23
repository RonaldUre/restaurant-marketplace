package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request de registro de restaurante (plataforma).
 * Contiene validaciones HTTP y estructura propia de la API pública.
 */
public record RegisterRestaurantRequest(

        // Perfil básico
        @NotBlank @Size(max = 120) String name,

        // kebab-case (igual formato que el VO Slug en dominio)
        @NotBlank @Size(max = 140)
        @Pattern(regexp = com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.validation.Patterns.SLUG)
        String slug,

        // Contacto (opcional)
        @Email @Size(max = 255) String email,
        @Size(max = 30) String phone,

        // Dirección (opcional; campos también opcionales)
        @Valid AddressRequest address,

        // Horarios (JSON) opcional
        @Size(max = 10_000) String openingHoursJson,

        // Credenciales del admin del tenant
        @NotBlank @Email @Size(max = 255) String adminEmail,
        @NotBlank @Size(min = 8, max = 100) @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$") String adminPassword
) {}
