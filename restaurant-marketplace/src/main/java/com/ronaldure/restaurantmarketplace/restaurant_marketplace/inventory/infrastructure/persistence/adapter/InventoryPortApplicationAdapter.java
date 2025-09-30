package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.adapter;

import java.util.List;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.InventoryPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;

public class InventoryPortApplicationAdapter implements InventoryPort{

    @Override
    public void reserve(TenantId tenantId, List<Reservation> reservations) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reserve'");
    }

    @Override
    public void release(TenantId tenantId, List<Reservation> reservations) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'release'");
    }

    @Override
    public void confirm(TenantId tenantId, List<Reservation> reservations) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'confirm'");
    }
    
}
