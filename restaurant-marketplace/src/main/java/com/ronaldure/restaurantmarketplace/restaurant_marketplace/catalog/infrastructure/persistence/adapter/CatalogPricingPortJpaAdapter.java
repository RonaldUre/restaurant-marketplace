// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/persistence/adapter/CatalogPricingPortJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.adapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

        List<Line> lines = new ArrayList<>(items.size());
        String currency = null;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Item it : items) {
            if (it == null) {
                throw new IllegalArgumentException("item cannot be null");
            }
            if (it.qty() <= 0) {
                throw new IllegalArgumentException("qty must be > 0 for productId=" + it.productId());
            }

            JpaProductEntity p = products
                    .findByIdAndTenantIdAndDeletedAtIsNull(it.productId(), tenantId.value())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "product not found for tenant, id=" + it.productId()));

            // Para el checkout público, evita vender no publicados
            if (!p.isPublished()) {
                throw new IllegalArgumentException("product is not published: id=" + it.productId());
            }

            if (currency == null) {
                currency = p.getPriceCurrency();
            } else if (!currency.equals(p.getPriceCurrency())) {
                throw new IllegalArgumentException(
                        "all products must share the same currency; found " + p.getPriceCurrency()
                                + " for productId=" + it.productId() + " but expected " + currency);
            }

            Money unitPrice = Money.of(p.getPriceAmount(), p.getPriceCurrency());
            BigDecimal lineTotalAmount = p.getPriceAmount().multiply(BigDecimal.valueOf(it.qty()));
            Money lineTotal = Money.of(lineTotalAmount, p.getPriceCurrency());

            lines.add(new Line(p.getId(), p.getName(), unitPrice, it.qty(), lineTotal));
            totalAmount = totalAmount.add(lineTotalAmount);
        }

        Money total = Money.of(totalAmount, currency);
        // MVP: sin inventario; si más adelante conectas stock, cámbialo a true según reglas
        boolean requiresInventory = false;

        return new PricedCatalog(lines, total, requiresInventory);
    }
}
