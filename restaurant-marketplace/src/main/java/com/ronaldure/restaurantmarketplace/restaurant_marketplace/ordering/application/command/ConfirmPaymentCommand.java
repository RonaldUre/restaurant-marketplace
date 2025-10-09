package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command;

/**
 * Command para confirmar pago de un pedido.
 * - Útil si integras webhook o pasos asíncronos; en el MVP lo usa el orquestador interno
 *   tras un charge() aprobado.
 */
public record ConfirmPaymentCommand(
        Long orderId,
        String transactionId    // txId retornado por la pasarela
) { }
