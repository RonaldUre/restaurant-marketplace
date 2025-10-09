// src/main/java/.../catalog/infrastructure/web/dto/UpdateProductRequest.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record UpdateProductRequest(
    @NotBlank
    @Size(max = 255)
    String name,

    @Size(max = 4000)
    String description,  // null/blank → empty() en factory

    @NotBlank
    @Size(max = 100)
    String category,

    @NotNull
    @DecimalMin("0.00")
    BigDecimal priceAmount,

    @NotBlank
    @Pattern(regexp = "^[A-Z]{3}$")
    String priceCurrency
) {}
