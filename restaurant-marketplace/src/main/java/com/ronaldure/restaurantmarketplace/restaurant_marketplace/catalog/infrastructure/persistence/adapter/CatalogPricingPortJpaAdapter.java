// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/persistence/adapter/CatalogPricingPortJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.adapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.entity.JpaProductEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.repository.ProductJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.CatalogPricingPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;

@Component
public class CatalogPricingPortJpaAdapter implements CatalogPricingPort {

    private final ProductJpaRepository products;

    public CatalogPricingPortJpaAdapter(ProductJpaRepository products) {
        this.products = products;
    }

    @Override
    @Transactional(readOnly = true)
    public PricedCatalog priceAndValidate(TenantId tenantId, List<Item> items) {
        Objects.requireNonNull(tenantId, "tenantId is required");
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("items cannot be null or empty");
        }

        // 1) Normalizar y validar inputs básicos
        //    - Rechazar nulos / qty <= 0
        //    - Agrupar por productId en caso de líneas duplicadas (sumar qty)
        Map<Long, Integer> qtyByProduct = new LinkedHashMap<>();
        for (Item it : items) {
            if (it == null) throw new IllegalArgumentException("item cannot be null");
            if (it.qty() <= 0) {
                throw new IllegalArgumentException("qty must be > 0 for productId=" + it.productId());
            }
            qtyByProduct.merge(it.productId(), it.qty(), Integer::sum);
        }

        // 2) Cargar productos en un solo SELECT
        List<Long> requestedIds = new ArrayList<>(qtyByProduct.keySet());
        List<JpaProductEntity> found = products.findAllActiveByTenantAndIdIn(tenantId.value(), requestedIds);

        // Map rápido por id
        Map<Long, JpaProductEntity> productById = new HashMap<>(found.size());
        for (JpaProductEntity p : found) productById.put(p.getId(), p);

        // Verificar faltantes (no existen / soft-deleted / otro tenant)
        List<Long> missing = new ArrayList<>();
        for (Long id : requestedIds) {
            if (!productById.containsKey(id)) missing.add(id);
        }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("product(s) not found for tenant: " + missing);
        }

        // 3) Validaciones de negocio en memoria + pricing
        String currency = null;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<Line> lines = new ArrayList<>(requestedIds.size());

        for (Long productId : requestedIds) {
            int qty = qtyByProduct.get(productId);

            JpaProductEntity p = productById.get(productId);

            // publicado
            if (!p.isPublished()) {
                throw new IllegalArgumentException("product is not published: id=" + productId);
            }

            // moneda consistente
            if (currency == null) {
                currency = p.getPriceCurrency();
            } else if (!currency.equals(p.getPriceCurrency())) {
                throw new IllegalArgumentException(
                        "all products must share the same currency; found "
                                + p.getPriceCurrency() + " for productId=" + productId
                                + " but expected " + currency);
            }

            // pricing
            Money unitPrice = Money.of(p.getPriceAmount(), p.getPriceCurrency());
            BigDecimal lineTotalAmount = p.getPriceAmount().multiply(BigDecimal.valueOf(qty));
            Money lineTotal = Money.of(lineTotalAmount, p.getPriceCurrency());

            lines.add(new Line(p.getId(), p.getName(), unitPrice, qty, lineTotal));
            totalAmount = totalAmount.add(lineTotalAmount);
        }

        Money total = Money.of(totalAmount, currency);
        boolean requiresInventory = true; // tu regla actual

        return new PricedCatalog(lines, total, requiresInventory);
    }
}
