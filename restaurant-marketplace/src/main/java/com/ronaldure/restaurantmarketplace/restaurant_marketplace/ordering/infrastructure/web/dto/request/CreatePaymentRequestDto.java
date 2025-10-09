// infrastructure/web/dto/request/CreatePaymentRequestDto.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para el cuerpo de la petición de creación de un pago.
 */
public record CreatePaymentRequestDto(
    @NotBlank(message = "paymentMethod is required")
    String paymentMethod
) {}