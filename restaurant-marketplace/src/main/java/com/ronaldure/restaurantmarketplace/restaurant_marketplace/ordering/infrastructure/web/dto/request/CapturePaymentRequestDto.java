// infrastructure/web/dto/request/CapturePaymentRequestDto.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para el cuerpo de la petición de captura de un pago.
 */
public record CapturePaymentRequestDto(
    @NotBlank(message = "paymentProviderOrderId is required")
    String paymentProviderOrderId // El ID que nos dio PayPal (token)
) {}