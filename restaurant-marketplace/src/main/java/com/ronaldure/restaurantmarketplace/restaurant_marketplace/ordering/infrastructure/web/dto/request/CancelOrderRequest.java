package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CancelOrderRequest(
        @NotNull(message = "orderId is required")
        Long orderId,

        @Size(max = 255, message = "reason too long")
        String reason
) { }
