// ordering/application/command/CreateOrderPaymentCommand.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command;

/**
 * Command para iniciar el proceso de pago de una orden.
 */
public record CreateOrderPaymentCommand(
    Long orderId,
    String paymentMethod // e.g., "PAYPAL"
) {}