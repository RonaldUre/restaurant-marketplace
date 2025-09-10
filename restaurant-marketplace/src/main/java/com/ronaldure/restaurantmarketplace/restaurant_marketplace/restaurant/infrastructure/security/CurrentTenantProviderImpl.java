package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.security;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security.TenantContext;

import java.util.Optional;

public class CurrentTenantProviderImpl implements CurrentTenantProvider {

    @Override
    public Optional<Long> currentTenantId() {
        return TenantContext.getTenantId();
    }
}