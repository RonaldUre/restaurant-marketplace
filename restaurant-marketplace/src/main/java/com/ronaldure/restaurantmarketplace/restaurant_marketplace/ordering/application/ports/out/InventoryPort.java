package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;

import java.util.List;

/** Reserva/Libera/Confirma stock por producto. */
public interface InventoryPort {

    void reserve(TenantId tenantId, List<Reservation> reservations);

    void release(TenantId tenantId, List<Reservation> reservations);

    void confirm(TenantId tenantId, List<Reservation> reservations);

    // qty > 0
    record Reservation(long productId, int qty) {}
}
