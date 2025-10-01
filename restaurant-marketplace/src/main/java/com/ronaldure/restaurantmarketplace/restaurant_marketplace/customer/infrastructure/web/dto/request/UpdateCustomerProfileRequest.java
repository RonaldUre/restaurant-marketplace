package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web.dto.request;

import jakarta.validation.constraints.*;

public record UpdateCustomerProfileRequest(
        // Nombre requerido (como en dominio)
        @NotBlank
        @Size(max = 120)
        String name,

        // Regla: null ⇒ no cambiar; "" ⇒ quitar teléfono; otro ⇒ validar patrón
        @Size(max = 30)
        @Pattern(regexp = "^[0-9+\\-()\\s]*$")
        String phone
) { }
