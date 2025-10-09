package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command;

import java.util.List;

/**
 * Command público para crear un pedido.
 * - El customerId se toma del JWT (CurrentUserProvider) en el controller.
 * - El tenant/restaurant se pasa explícitamente.
 * - Los precios se calculan vía CatalogPricingPort; aquí solo vienen items y método de pago.
 */
public record PlaceOrderCommand(
        Long restaurantId,
        List<Item> items
) {
    /** Ítems solicitados (qty > 0). */
    public record Item(Long productId, int qty) { }
}
