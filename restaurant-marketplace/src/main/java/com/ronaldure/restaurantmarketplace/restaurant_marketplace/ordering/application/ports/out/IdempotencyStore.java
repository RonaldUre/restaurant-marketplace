package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;

import java.util.Optional;

/**
 * Store auxiliar para evitar duplicados de PlaceOrder.
 * Clave única: (tenantId, customerId, key).
 */
public interface IdempotencyStore {

    Optional<Existing> find(TenantId tenantId, UserId customerId, String key);

    void save(TenantId tenantId, UserId customerId, String key, long orderId);

    record Existing(String key, long orderId) {}
}
