// src/main/java/.../inventory/infrastructure/web/dto/request/AdjustStockRequest.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.web.dto.request;

import jakarta.validation.constraints.*;
import jakarta.validation.constraints.AssertTrue;

/** Admin-only. Positive increases, negative decreases (limited only). */
public record AdjustStockRequest(
        @NotNull Integer delta,     // must be non-zero
        @Size(max = 500) String reason
) {
    @AssertTrue(message = "delta must be non-zero")
    public boolean isNonZero() { return delta != null && delta != 0; }
}
