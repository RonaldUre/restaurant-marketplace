package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request para actualizar el perfil del restaurante (tenant actual).
 * Null = "sin cambio". Usa AddressRequest anidado (también null = "sin cambio").
 */
public record UpdateRestaurantProfileRequest(

        @Size(min = 1, max = 120) String name,

        @Size(min = 1, max = 140)
        @Pattern(regexp = com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.validation.Patterns.SLUG)
        String slug,

        @Email @Size(max = 255) String email,
        @Size(max = 30, min = 1) String phone,

        @Valid AddressRequest address,

        // Si se envía, reemplaza horarios; si es null, no cambia
        @Size(max = 10_000) String openingHoursJson
) {}
