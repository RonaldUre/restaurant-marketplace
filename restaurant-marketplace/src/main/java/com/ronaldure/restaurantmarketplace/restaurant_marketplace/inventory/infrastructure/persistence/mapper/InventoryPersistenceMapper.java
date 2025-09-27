// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/inventory/infrastructure/persistence/mapper/InventoryPersistenceMapper.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.ProductId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.InventoryItem;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.model.vo.InventoryItemId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.entity.JpaInventoryEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Component;

/** Maps domain <-> JPA for InventoryItem. */
@Component
public class InventoryPersistenceMapper {

    // Domain -> JPA
    public JpaInventoryEntity toEntity(InventoryItem item) {
        JpaInventoryEntity e = new JpaInventoryEntity();
        if (item.id() != null) e.setId(item.id().value());
        e.setTenantId(item.tenantId().value());
        e.setProductId(item.productId().value());
        e.setAvailable(item.available());                   // null => unlimited
        e.setReserved(item.reserved().value());
        // version is handled by JPA; createdAt/updatedAt via @Creation/@UpdateTimestamp
        return e;
    }

    // JPA -> Domain
    public InventoryItem toDomain(JpaInventoryEntity e) {
        return InventoryItem.rehydrate(
                InventoryItemId.of(e.getId()),
                TenantId.of(e.getTenantId()),
                ProductId.of(e.getProductId()),
                e.getAvailable(),
                e.getReserved(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
