package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentTenantProviderImpl implements CurrentTenantProvider {

    @Override
    public Optional<TenantId> findCurrent() {
        // TenantContext exposes Optional<Long>; map it to the VO
        return TenantContext.getTenantId().map(TenantId::of);
    }
}
