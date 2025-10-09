// src/main/java/.../inventory/infrastructure/web/dto/request/SwitchToLimitedRequest.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.web.dto.request;

import jakarta.validation.constraints.*;

public record SwitchToLimitedRequest(
        @NotNull
        @Min(0)
        Integer initialAvailable
) {}
