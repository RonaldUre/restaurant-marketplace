package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;

import java.util.List;

/**
 * Valida pertenencia de productos al tenant y calcula precios vigentes
 * para construir el snapshot de líneas del pedido.
 */
public interface CatalogPricingPort {

    PricedCatalog priceAndValidate(TenantId tenantId, List<Item> items);

    // ---- Tipos auxiliares del puerto ----
    record Item(long productId, int qty) {}

    record Line(long productId, String name, Money unitPrice, int qty, Money lineTotal) {}

    record PricedCatalog(List<Line> lines, Money total, boolean requiresInventory) {}
}
