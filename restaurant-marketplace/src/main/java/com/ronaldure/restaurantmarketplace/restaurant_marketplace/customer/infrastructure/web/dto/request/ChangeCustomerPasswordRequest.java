package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web.dto.request;

import jakarta.validation.constraints.*;

public record ChangeCustomerPasswordRequest(
        @NotBlank
        String currentPassword,

        @NotBlank
        @Size(min = 8, max = 255)
        String newPassword
) { }
