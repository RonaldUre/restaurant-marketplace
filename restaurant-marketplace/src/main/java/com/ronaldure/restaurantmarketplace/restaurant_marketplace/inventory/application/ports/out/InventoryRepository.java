package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.ProductId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.InventoryItem;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;

import java.util.Optional;

public interface InventoryRepository {

    // Bootstrap / existencia
    boolean existsByTenantAndProduct(TenantId tenantId, Long productId);

    InventoryItem createUnlimitedIfAbsent(TenantId tenantId, ProductId productId);

    Optional<InventoryItem> findByTenantAndProduct(TenantId tenantId, Long productId);

    // Mutaciones de alto nivel (si usas locking optimista dentro)
    InventoryItem save(InventoryItem item);

    // Operaciones atómicas opcionales (si implementas SQL directo en adapter)
    boolean reserveAtomic(TenantId tenantId, Long productId, int qty);
    boolean confirmAtomic(TenantId tenantId, Long productId, int qty);
    boolean releaseAtomic(TenantId tenantId, Long productId, int qty);
    boolean adjustAtomic(TenantId tenantId, Long productId, int delta);
    boolean switchToLimited(TenantId tenantId, Long productId, int initialAvailable);
    boolean switchToUnlimited(TenantId tenantId, Long productId);

    // Nota: El handler de listado usa un port separado de consulta (proyección + pageable).
}
