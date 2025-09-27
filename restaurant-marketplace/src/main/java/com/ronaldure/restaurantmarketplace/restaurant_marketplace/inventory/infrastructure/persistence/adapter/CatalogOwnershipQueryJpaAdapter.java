// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/inventory/infrastructure/persistence/adapter/CatalogOwnershipQueryJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out.CatalogOwnershipQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.projection.ProductBasicProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.repository.CatalogReadJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/** Lightweight read into Catalog using interface-based projection (no entity coupling). */
@Component
public class CatalogOwnershipQueryJpaAdapter implements CatalogOwnershipQuery {

    private final CatalogReadJpaRepository repo;

    public CatalogOwnershipQueryJpaAdapter(CatalogReadJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean belongsToTenant(TenantId tenantId, Long productId) {
        return repo.existsByIdAndTenantIdAndDeletedAtIsNull(productId, tenantId.value());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductBasic> findProductBasic(TenantId tenantId, Long productId) {
        return repo.findProductBasic(tenantId.value(), productId).map(this::map);
    }

    private ProductBasic map(ProductBasicProjection p) {
        return new ProductBasic(p.getProductId(), p.getSku(), p.getName(), p.getCategory());
    }
}
