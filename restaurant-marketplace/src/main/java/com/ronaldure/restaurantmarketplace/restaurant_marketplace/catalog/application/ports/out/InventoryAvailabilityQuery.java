package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out;

import java.util.Collection;
import java.util.Map;

public interface InventoryAvailabilityQuery {
    /** Disponibilidad para un solo producto de un restaurante. */
    boolean isAvailable(Long restaurantId, Long productId);

    /** Disponibilidad en lote (optimiza el listado). */
     Map<Long, Boolean> areAvailable(Long restaurantId, Collection<Long> productIds);
}