// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/inventory/infrastructure/persistence/adapter/InventoryRepositoryJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.ProductId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out.InventoryRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.InventoryItem;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.entity.JpaInventoryEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.mapper.InventoryPersistenceMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.repository.InventoryJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
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
    public InventoryItem createUnlimitedIfAbsent(TenantId tenantId, ProductId productId) {
        // 0) Defensas rápidas (los VO ya validan >0, aquí validamos no-nulos)
        Objects.requireNonNull(tenantId, "tenantId is required");
        Objects.requireNonNull(productId, "productId is required");

        // 1) Fast path: si existe, devuélvelo
        Optional<JpaInventoryEntity> existing = jpa.findByTenantIdAndProductId(tenantId.value(), productId.value());
        if (existing.isPresent()) {
            return mapper.toDomain(existing.get()); // mapper debe mapear id técnico también
        }

        // 2) Intento de creación; 'available = NULL' => unlimited (política de stock)
        JpaInventoryEntity entity = new JpaInventoryEntity(
                null, // id (auto)
                tenantId.value(),
                productId.value(),
                null, // available = NULL => unlimited
                0, // reserved
                null, // version => que JPA @Version lo maneje
                null,
                null);

        try {
            JpaInventoryEntity saved = jpa.save(entity);
            // fuerza el flush aquí para capturar violación UNIQUE dentro del try/catch
            jpa.flush();

            return mapper.toDomain(saved); // el mapper debe propagar saved.getId()
        } catch (DataIntegrityViolationException dup) {
            // Carrera entre threads/instancias: alguien lo insertó antes que nosotros
            return mapper.toDomain(
                    jpa.findByTenantIdAndProductId(tenantId.value(), productId.value())
                            .orElseThrow(() -> dup) // extremadamente raro que no esté
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
    // 1) Cargar entidad gestionada con su @Version actual
    JpaInventoryEntity managed;

    if (item.id() != null) {
        // Si el agregado ya trae id técnico, úsalo
        managed = jpa.findById(item.id().value())
                .orElseThrow(() -> new IllegalStateException(
                        "Inventory row not found by id=" + item.id().value()
                ));
    } else {
        // Fallback por identidad de negocio (tenant + product)
        managed = jpa.findByTenantIdAndProductId(item.tenantId().value(), item.productId().value())
                .orElseThrow(() -> new IllegalStateException(
                        "Inventory row not found for tenant=" + item.tenantId().value()
                        + " product=" + item.productId().value()
                ));
    }

    // 2) Copiar SOLO campos mutables (deja que JPA maneje version/fechas)
    managed.setAvailable(item.available());                 // null => unlimited
    managed.setReserved(item.reserved().value());           // >= 0

    // 3) Guardar/flush (opcional; por ser managed bastaría con flush al final de la TX)
    JpaInventoryEntity saved = jpa.save(managed);

    // 4) Volver a dominio rehidratado (ya trae id y timestamps correctos)
    return mapper.toDomain(saved);
}

    // --- Atomic ops (no usadas en MVP; devolvemos false para indicar no soportado)
    // ---

    @Override
    public boolean reserveAtomic(TenantId t, Long p, int qty) {
        return false;
    }

    @Override
    public boolean confirmAtomic(TenantId t, Long p, int qty) {
        return false;
    }

    @Override
    public boolean releaseAtomic(TenantId t, Long p, int qty) {
        return false;
    }

    @Override
    public boolean adjustAtomic(TenantId t, Long p, int d) {
        return false;
    }

    @Override
    public boolean switchToLimited(TenantId t, Long p, int i) {
        return false;
    }

    @Override
    public boolean switchToUnlimited(TenantId t, Long p) {
        return false;
    }
}
