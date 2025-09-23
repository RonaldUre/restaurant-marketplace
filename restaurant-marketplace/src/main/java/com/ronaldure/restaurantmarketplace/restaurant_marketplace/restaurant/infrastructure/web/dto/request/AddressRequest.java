package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Address anidado para requests web. Todos los campos son opcionales. */
public record AddressRequest(
        @Size(max = 255) String line1,
        @Size(max = 255) String line2,
        @Size(max = 120) String city,
        @Size(max = 2) @Pattern(regexp = "^[A-Za-z]{2}$")  String country,     // ISO-3166-1 alpha-2
        @Size(max = 20)  String postalCode
) {}
