package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request dedicado para actualizar solo los horarios (JSON). */
public record UpdateOpeningHoursRequest(
        @NotBlank
        @Size(max = 10_000)
        String openingHoursJson
) {}
