// src/main/java/.../inventory/infrastructure/mapper/InventoryWebMapper.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.query.ListInventoryAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.view.InventoryAdminItemView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.web.dto.request.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.web.dto.response.InventoryAdminItemResponse;
import org.springframework.stereotype.Component;

/** Web ↔ Application mapper for Inventory web layer. */
@Component
public class InventoryWebMapper {

    // ===== Web -> Commands =====
    public AdjustStockCommand toCommand(Long productId, AdjustStockRequest req) {
        return new AdjustStockCommand(productId, req.delta());
    }

    public SwitchToLimitedCommand toCommand(Long productId, SwitchToLimitedRequest req) {
        return new SwitchToLimitedCommand(productId, req.initialAvailable());
    }

    public SwitchToUnlimitedCommand toCommand(Long productId) {
        return new SwitchToUnlimitedCommand(productId);
    }

    // ===== Web -> Query Params =====
    public ListInventoryAdminQueryParams toParams(ListInventoryAdminRequest req) {
        return new ListInventoryAdminQueryParams(
                nullIfBlank(req.sku()),
                req.productId(),
                nullIfBlank(req.category()),
                defaultInt(req.page(), 0),
                defaultInt(req.size(), 20),
                nullIfBlank(req.sortBy()),
                nullIfBlank(req.sortDir())
        );
    }

    // ===== View -> Response =====
    public InventoryAdminItemResponse toAdminItemResponse(InventoryAdminItemView view) {
        return new InventoryAdminItemResponse(
                view.productId(),
                view.sku(),
                view.name(),
                view.category(),
                view.available(),
                view.reserved(),
                view.unlimited(),
                view.createdAt(),
                view.updatedAt()
        );
    }

    // ---- helpers ----
    private static String nullIfBlank(String s) { return (s == null || s.isBlank()) ? null : s; }
    private static int defaultInt(Integer v, int dft) { return v == null ? dft : v; }
}
