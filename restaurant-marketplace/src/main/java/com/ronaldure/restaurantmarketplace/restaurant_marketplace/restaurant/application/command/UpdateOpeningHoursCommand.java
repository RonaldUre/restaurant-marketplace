package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Command dedicado para actualizar solo los horarios (JSON). */
public record UpdateOpeningHoursCommand(
        @NotBlank
        @Size(max = 10_000) 
        String openingHoursJson
) {}
