// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/persistence/adapter/AdminOrderDetailQueryJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.AdminOrderDetailQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.projection.OrderAdminDetailProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.repository.OrderJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class AdminOrderDetailQueryJpaAdapter implements AdminOrderDetailQuery {

    private final OrderJpaRepository jpa;

    public AdminOrderDetailQueryJpaAdapter(OrderJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDetailView> findById(TenantId tenantId, Long orderId) {
        return jpa.findProjectedByIdAndTenantId(orderId, tenantId.value())
                  .map(AdminOrderDetailQueryJpaAdapter::map);
    }

    private static OrderDetailView map(OrderAdminDetailProjection v) {
        var lines = v.getLines().stream()
                .map(l -> new OrderDetailView.LineView(
                        l.getProductId(),
                        l.getProductName(),
                        l.getUnitPriceAmount(),
                        l.getUnitPriceCurrency(),
                        l.getQty(),
                        l.getLineTotalAmount()
                ))
                .toList();

        return OrderDetailView.of(
                v.getId(),
                v.getTenantId(),
                v.getCustomerId(),
                v.getStatus(),
                v.getTotalAmount(),
                v.getCurrency(),
                v.getCreatedAt(),
                lines
        );
    }
}
