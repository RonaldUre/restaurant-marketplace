package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request para verificación de disponibilidad de slug (?value=). */
public record CheckSlugAvailabilityRequest(
        @NotBlank @Size(min = 1, max = 140) String value
) {}
