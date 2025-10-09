// infrastructure/web/dto/response/CreatePaymentResponseDto.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.response;

/**
 * DTO para la respuesta de la creación de un pago. Contiene la URL de aprobación.
 */
public record CreatePaymentResponseDto(String approvalUrl) {}