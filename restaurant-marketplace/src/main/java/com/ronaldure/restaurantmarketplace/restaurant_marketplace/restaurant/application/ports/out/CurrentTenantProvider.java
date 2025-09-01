// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/ports/out/CurrentTenantProvider.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out;

import java.util.Optional;

/**
 * OUT port to obtain the current tenant context (e.g., from JWT).
 * Used by admin use cases. Public queries should not rely on tenant context.
 */
public interface CurrentTenantProvider {

    /**
     * @return tenantId if present in the current security context (e.g., RESTAURANT_ADMIN), otherwise empty.
     */
    Optional<Long> currentTenantId();
}