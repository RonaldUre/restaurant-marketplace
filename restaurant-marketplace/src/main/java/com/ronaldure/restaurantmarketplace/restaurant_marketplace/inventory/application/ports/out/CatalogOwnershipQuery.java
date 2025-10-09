package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;

import java.util.Optional;

/**
 * Lectura ligera hacia Catálogo para:
 * 1) Validar que productId pertenece al tenant (seguridad/aislamiento).
 * 2) Traer datos básicos (sku, name, category) para componer la vista admin.
 */
public interface CatalogOwnershipQuery {

    boolean belongsToTenant(TenantId tenantId, Long productId);

    Optional<ProductBasic> findProductBasic(TenantId tenantId, Long productId);

    record ProductBasic(
            Long productId,
            String sku,
            String name,
            String category
    ) { }
}
