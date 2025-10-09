// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/notifications/infrastructure/adapter/OrderTenantLookupAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.application.errors.NotificationFailedException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.projection.OrderBasicProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.repository.OrderJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderTenantLookupAdapter {

    private final OrderJpaRepository orders;

    public OrderTenantLookupAdapter(OrderJpaRepository orders) {
        this.orders = orders;
    }

public Long resolveTenantIdOrThrow(Long orderId) {
    return orders.findProjectedById(orderId)
        .map(OrderBasicProjection::getTenantId)
        .orElseThrow(() -> new NotificationFailedException(orderId, "Order not found when resolving tenantId"));
}
}
