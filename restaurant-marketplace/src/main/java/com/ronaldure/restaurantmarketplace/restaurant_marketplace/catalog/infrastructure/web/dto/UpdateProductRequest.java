// src/main/java/.../catalog/infrastructure/web/dto/UpdateProductRequest.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record UpdateProductRequest(
    @Size(max = 255)
    String name,

    @Size(max = 4000)
    String description,  // null/blank → empty() en factory

    @Size(max = 100)
    String category,

    @DecimalMin(value = "0.00")
    BigDecimal priceAmount,

    @Pattern(regexp = "^[A-Z]{3}$")
    String priceCurrency
) {}
