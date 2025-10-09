package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web.dto.request;

import jakarta.validation.constraints.*;

public record RegisterCustomerRequest(
        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(max = 120)
        String name,

        // Opcional; NULL ⇒ sin teléfono
        @Size(max = 30)
        @Pattern(regexp = "^[0-9+\\-()\\s]*$")
        String phone,

        @NotBlank
        @Size(min = 8, max = 255)
        String password
) { }
