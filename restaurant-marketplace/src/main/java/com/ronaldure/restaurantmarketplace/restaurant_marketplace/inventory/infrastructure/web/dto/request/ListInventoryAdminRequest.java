// src/main/java/.../inventory/infrastructure/web/dto/request/ListInventoryAdminRequest.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.web.dto.request;

import jakarta.validation.constraints.*;

public record ListInventoryAdminRequest(
        @Size(max = 100) String sku,
        @Min(1) Long productId,                 // optional (exact)
        @Size(max = 100) String category,

        // Pagination
        @Min(0) Integer page,
        @Min(1) Integer size,

        // Sorting (whitelist aligned with adapter)
        @Pattern(regexp = "^(name|sku|category|available|reserved|updatedAt)$")
        String sortBy,
        @Pattern(regexp = "^(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE)
        String sortDir
) {}
