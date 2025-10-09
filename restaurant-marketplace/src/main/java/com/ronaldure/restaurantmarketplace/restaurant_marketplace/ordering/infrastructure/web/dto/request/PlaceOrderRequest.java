package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlaceOrderRequest(
        @NotNull(message = "restaurantId is required")
        Long restaurantId,

        @NotNull @Size(min = 1, message = "at least one item is required")
        List<@Valid Item> items
) {
    public record Item(
            @NotNull(message = "productId is required")
            Long productId,

            @NotNull @Positive(message = "qty must be > 0")
            Integer qty
    ) { }
}
