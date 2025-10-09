package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.errors.ForbiddenOperationException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;

import java.util.Optional;

public interface CurrentTenantProvider {
    Optional<TenantId> findCurrent();

    default TenantId requireCurrent() {
        return findCurrent().orElseThrow(ForbiddenOperationException::tenantContextRequired);
    }
}
