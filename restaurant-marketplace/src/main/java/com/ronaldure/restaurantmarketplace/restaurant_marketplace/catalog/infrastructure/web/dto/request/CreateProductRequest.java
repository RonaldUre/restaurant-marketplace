// src/main/java/.../catalog/infrastructure/web/dto/CreateProductRequest.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateProductRequest(
    @NotBlank
    @Size(max = 64)
    @Pattern(regexp = "^[A-Za-z0-9._-]{1,64}$")  // mismo set que Sku
    String sku,

    @NotBlank
    @Size(max = 255)
    String name,

    @Size(max = 4000)
    String description,  // null/blank → empty() en factory

    @NotBlank
    @Size(max = 100)
    String category,

    @NotNull
    @DecimalMin(value = "0.00")
    BigDecimal priceAmount,

    @NotBlank
    @Pattern(regexp = "^[A-Z]{3}$") // ISO-4217
    String priceCurrency
) {}
