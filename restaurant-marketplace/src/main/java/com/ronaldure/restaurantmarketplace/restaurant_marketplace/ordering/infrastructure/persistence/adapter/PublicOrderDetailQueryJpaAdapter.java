// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/persistence/adapter/PublicOrderDetailQueryJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.PublicOrderDetailQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.projection.OrderPublicDetailProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.repository.OrderJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class PublicOrderDetailQueryJpaAdapter implements PublicOrderDetailQuery {

    private final OrderJpaRepository jpa;

    public PublicOrderDetailQueryJpaAdapter(OrderJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDetailView> findOwned(Long orderId, UserId ownerId) {
        return jpa.findProjectedByIdAndCustomerId(orderId, Long.parseLong(ownerId.value()))
                  .map(PublicOrderDetailQueryJpaAdapter::map);
    }

    private static OrderDetailView map(OrderPublicDetailProjection v) {
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

