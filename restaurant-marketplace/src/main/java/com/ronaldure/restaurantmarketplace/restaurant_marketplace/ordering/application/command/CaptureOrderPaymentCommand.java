// ordering/application/command/CaptureOrderPaymentCommand.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command;

/**
 * Command para capturar/finalizar un pago que ya fue aprobado por el cliente.
 */
public record CaptureOrderPaymentCommand(
    Long orderId,
    String paymentProviderOrderId, // El ID de la orden que nos dio PayPal
    String idempotencyKey
) {}