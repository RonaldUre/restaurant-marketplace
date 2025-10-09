package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command;

/**
 * Command para cancelar un pedido.
 * - En el MVP: solo si está en estado CREATED.
 * - reason es opcional, útil para auditoría (admin/customer).
 */
public record CancelOrderCommand(
        Long orderId,
        String reason
) { }
