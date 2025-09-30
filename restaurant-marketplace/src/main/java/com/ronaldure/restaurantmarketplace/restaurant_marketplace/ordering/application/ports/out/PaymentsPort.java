package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;

/**
 * Abstracción de pasarela de pagos. Implementada en el módulo `payments`.
 */
public interface PaymentsPort {

    ChargeResult charge(ChargeRequest request);

    // ---- Tipos auxiliares ----
    record ChargeRequest(long orderId, TenantId tenantId, Money amount, String method) {}

    /**
     * approved = true -> txId presente
     * approved = false -> reason presente
     */
    record ChargeResult(boolean approved, String txId, String reason) {}
}
