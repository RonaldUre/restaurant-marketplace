package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotNull;

public record ConfirmPaymentRequest(
        @NotNull(message = "orderId is required")
        Long orderId
        // Si más adelante quieres recibir un txId externo, agrégalo aquí.
) { }
