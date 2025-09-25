// src/main/java/.../inventory/application/factory/InventoryFactory.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.factory;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command.SwitchToLimitedCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.InventoryItem;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.model.vo.InventoryItemId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Quantity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.ProductId;
import org.springframework.stereotype.Component;

@Component
public class InventoryFactory {

    /** Construye un InventoryItem ilimitado (default al crearse un producto). */
    public InventoryItem createUnlimited(TenantId tenantId, ProductId productId) {
        return InventoryItem.createUnlimited(tenantId, productId);
    }

    /** Construye un InventoryItem limitado a partir de un command explícito. */
    public InventoryItem createLimited(TenantId tenantId, ProductId productId, SwitchToLimitedCommand cmd) {
        return InventoryItem.createLimited(tenantId, productId, Quantity.of(cmd.initialAvailable()));
    }

    /** Rehidratación explícita (por si necesitas mapear manualmente en tests). */
    public InventoryItem rehydrate(InventoryItemId id,
                                   TenantId tenantId,
                                   ProductId productId,
                                   Integer available,
                                   int reserved,
                                   java.time.Instant createdAt,
                                   java.time.Instant updatedAt) {
        return InventoryItem.rehydrate(id, tenantId, productId, available, reserved, createdAt, updatedAt);
    }
}
