package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.events;

import java.time.Instant;
import java.util.Objects;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.ProductId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;

public record ProductCreatedEvent(
    TenantId tenantId,
    ProductId productId,
    Instant occurredOn
) implements DomainEvent {

    public ProductCreatedEvent {
        Objects.requireNonNull(tenantId, "tenantId is required");
        Objects.requireNonNull(productId, "productId is required");
        // Los VO ya garantizan > 0; no dupliques eso aquí.
        occurredOn = (occurredOn == null) ? Instant.now() : occurredOn;
    }

    public static ProductCreatedEvent of(TenantId tenantId, ProductId productId) {
        return new ProductCreatedEvent(tenantId, productId, Instant.now());
    }
}
