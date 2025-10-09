// src/main/java/.../inventory/application/mapper/InventoryApplicationMapper.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out.CatalogOwnershipQuery.ProductBasic;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.view.InventoryAdminItemView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.InventoryItem;
import org.springframework.stereotype.Component;

@Component
public class InventoryApplicationMapper {

    /** Compose view from aggregate + product basic info (join lógico en aplicación). */
    public InventoryAdminItemView toAdminItemView(InventoryItem item, ProductBasic product) {
        Integer available = item.available(); // null => unlimited
        return new InventoryAdminItemView(
                product.productId(),
                product.sku(),
                product.name(),
                product.category(),
                available,
                item.reserved().value(),
                available == null,
                item.createdAt(),
                item.updatedAt()
        );
    }

    /** Para listados basados en proyecciones ya enriquecidas desde infra. */
    public InventoryAdminItemView fromProjection(Long productId,
                                                 String sku,
                                                 String name,
                                                 String category,
                                                 Integer available,
                                                 Integer reserved,
                                                 java.time.Instant createdAt,
                                                 java.time.Instant updatedAt) {
        return new InventoryAdminItemView(
                productId, sku, name, category,
                available, reserved,
                available == null,
                createdAt, updatedAt
        );
    }
}
