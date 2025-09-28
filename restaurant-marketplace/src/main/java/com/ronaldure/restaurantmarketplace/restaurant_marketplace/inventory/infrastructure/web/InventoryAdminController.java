// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/inventory/infrastructure/web/InventoryAdminController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.in.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.query.ListInventoryAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.view.InventoryAdminItemView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.mapper.InventoryWebMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.web.dto.request.AdjustStockRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.web.dto.request.ListInventoryAdminRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.web.dto.request.SwitchToLimitedRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.web.dto.response.InventoryAdminItemResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/inventory")
public class InventoryAdminController {

    private final ListInventoryAdminQuery listInventoryAdmin;
    private final AdjustStockUseCase adjustStock;
    private final SwitchToLimitedUseCase switchToLimited;
    private final SwitchToUnlimitedUseCase switchToUnlimited;
    private final InventoryWebMapper webMapper;

    public InventoryAdminController(ListInventoryAdminQuery listInventoryAdmin,
                                    AdjustStockUseCase adjustStock,
                                    SwitchToLimitedUseCase switchToLimited,
                                    SwitchToUnlimitedUseCase switchToUnlimited,
                                    InventoryWebMapper webMapper) {
        this.listInventoryAdmin = listInventoryAdmin;
        this.adjustStock = adjustStock;
        this.switchToLimited = switchToLimited;
        this.switchToUnlimited = switchToUnlimited;
        this.webMapper = webMapper;
    }

    // Listado admin (paginado + filtros + sort)
    @GetMapping
    public ResponseEntity<PageResponse<InventoryAdminItemResponse>> list(
            @Valid @ModelAttribute ListInventoryAdminRequest query) {

        ListInventoryAdminQueryParams params = webMapper.toParams(query);
        PageResponse<InventoryAdminItemView> result = listInventoryAdmin.list(params);

        List<InventoryAdminItemResponse> items = result.items().stream()
                .map(webMapper::toAdminItemResponse)
                .toList();

        return ResponseEntity.ok(new PageResponse<>(items, result.totalElements(), result.totalPages()));
    }

    // Ajuste de stock (solo limitado)
    @PostMapping("/{productId}/adjust")
    public ResponseEntity<InventoryAdminItemResponse> adjust(@PathVariable("productId") Long productId,
                                                             @RequestBody @Valid AdjustStockRequest body) {
        var view = adjustStock.adjust(webMapper.toCommand(productId, body));
        return ResponseEntity.ok(webMapper.toAdminItemResponse(view));
    }

    // Cambiar a stock limitado
    @PostMapping("/{productId}/switch-to-limited")
    public ResponseEntity<InventoryAdminItemResponse> switchToLimited(@PathVariable("productId") Long productId,
                                                                      @RequestBody @Valid SwitchToLimitedRequest body) {
        var view = switchToLimited.switchToLimited(webMapper.toCommand(productId, body));
        return ResponseEntity.ok(webMapper.toAdminItemResponse(view));
    }

    // Cambiar a stock ilimitado (requiere reserved == 0)
    @PostMapping("/{productId}/switch-to-unlimited")
    public ResponseEntity<InventoryAdminItemResponse> switchToUnlimited(@PathVariable("productId") Long productId) {
        var view = switchToUnlimited.switchToUnlimited(webMapper.toCommand(productId));
        return ResponseEntity.ok(webMapper.toAdminItemResponse(view));
    }
}
