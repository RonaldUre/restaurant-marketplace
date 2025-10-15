package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out.InventoryAvailabilityQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.entity.JpaInventoryEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.repository.InventoryJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Adapter JPA para disponibilidad pública.
 * Regla:
 *  - Row inexistente => se considera "unlimited" => available = true
 *  - available == null (unlimited) => true
 *  - available != null => (available - reserved) > 0
 */
@Component
public class InventoryAvailabilityQueryJpaAdapter implements InventoryAvailabilityQuery {

    private final InventoryJpaRepository repo;

    public InventoryAvailabilityQueryJpaAdapter(InventoryJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAvailable(Long restaurantId, Long productId) {
        Optional<JpaInventoryEntity> row = repo.findByTenantIdAndProductId(restaurantId, productId);
        if (row.isEmpty()) return true; // por defecto ilimitado
        Integer avail = row.get().getAvailable();
        Integer resv  = row.get().getReserved() == null ? 0 : row.get().getReserved();
        return (avail == null) || (avail - resv > 0);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Boolean> areAvailable(Long restaurantId, Collection<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) return Collections.emptyMap();

        // Cargar filas existentes en inventario (puede que falten algunas => default unlimited).
        List<JpaInventoryEntity> rows = repo.findByTenantIdAndProductIdIn(restaurantId, productIds);

        Map<Long, Boolean> fromDb = rows.stream().collect(Collectors.toMap(
                JpaInventoryEntity::getProductId,
                e -> {
                    Integer avail = e.getAvailable();
                    Integer resv  = e.getReserved() == null ? 0 : e.getReserved();
                    return (avail == null) || (avail - resv > 0);
                },
                // merge function (no debería haber duplicados)
                (a, b) -> a,
                LinkedHashMap::new
        ));

        // Completar con default=true para los que no tengan fila (unlimited por defecto)
        Map<Long, Boolean> result = new LinkedHashMap<>();
        for (Long id : productIds) {
            result.put(id, fromDb.getOrDefault(id, true));
        }
        return result;
    }
}
