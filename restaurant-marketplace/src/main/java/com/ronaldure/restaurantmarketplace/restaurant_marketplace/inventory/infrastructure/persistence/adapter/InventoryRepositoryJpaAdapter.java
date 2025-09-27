// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/inventory/infrastructure/persistence/adapter/InventoryRepositoryJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out.InventoryRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.InventoryItem;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.model.vo.InventoryItemId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.entity.JpaInventoryEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.mapper.InventoryPersistenceMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.repository.InventoryJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/** JPA adapter (optimistic locking). Atomic SQL ops no-op en este MVP. */
@Component
public class InventoryRepositoryJpaAdapter implements InventoryRepository {

    private final InventoryJpaRepository jpa;
    private final InventoryPersistenceMapper mapper;

    public InventoryRepositoryJpaAdapter(InventoryJpaRepository jpa,
                                         InventoryPersistenceMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    // --- Bootstrap / existencia ---

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTenantAndProduct(TenantId tenantId, Long productId) {
        return jpa.existsByTenantIdAndProductId(tenantId.value(), productId);
    }

    @Override
    @Transactional
    public InventoryItem createUnlimitedIfAbsent(TenantId tenantId, Long productId) {
        // Fast path: si existe, devuélvelo
        Optional<JpaInventoryEntity> existing =
                jpa.findByTenantIdAndProductId(tenantId.value(), productId);
        if (existing.isPresent()) return mapper.toDomain(existing.get());

        // Intento de creación; si choca UNIQUE, leemos el que ya existe
        JpaInventoryEntity e = new JpaInventoryEntity(
                null,
                tenantId.value(),
                productId,
                null,   // available = NULL => unlimited
                0,      // reserved
                0,      // version (JPA lo maneja)
                null,
                null
        );
        try {
            JpaInventoryEntity saved = jpa.save(e);
            InventoryItem domain = mapper.toDomain(saved);
            // asignar id técnico al aggregate por paridad
            domain.assignId(InventoryItemId.of(saved.getId()));
            return domain;
        } catch (DataIntegrityViolationException dup) {
            // Carrera entre threads/instancias: leer el existente
            return mapper.toDomain(
                    jpa.findByTenantIdAndProductId(tenantId.value(), productId).orElseThrow()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InventoryItem> findByTenantAndProduct(TenantId tenantId, Long productId) {
        return jpa.findByTenantIdAndProductId(tenantId.value(), productId).map(mapper::toDomain);
    }

    // --- Mutaciones con locking optimista ---

    @Override
    @Transactional
    public InventoryItem save(InventoryItem item) {
        JpaInventoryEntity entity = mapper.toEntity(item);
        JpaInventoryEntity saved = jpa.save(entity); // @Version maneja conflictos
        InventoryItem rehydrated = mapper.toDomain(saved);
        rehydrated.assignId(InventoryItemId.of(saved.getId()));
        return rehydrated;
    }

    // --- Atomic ops (no usadas en MVP; devolvemos false para indicar no soportado) ---

    @Override public boolean reserveAtomic(TenantId t, Long p, int qty) { return false; }
    @Override public boolean confirmAtomic(TenantId t, Long p, int qty) { return false; }
    @Override public boolean releaseAtomic(TenantId t, Long p, int qty) { return false; }
    @Override public boolean adjustAtomic(TenantId t, Long p, int d)   { return false; }
    @Override public boolean switchToLimited(TenantId t, Long p, int i) { return false; }
    @Override public boolean switchToUnlimited(TenantId t, Long p)      { return false; }
}
