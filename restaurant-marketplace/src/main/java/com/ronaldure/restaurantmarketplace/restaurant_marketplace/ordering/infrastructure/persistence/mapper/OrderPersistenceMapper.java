// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/persistence/mapper/OrderPersistenceMapper.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.Order;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.entity.JpaOrderEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.entity.JpaOrderLineEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Quantity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderPersistenceMapper {

    // ---------- Domain -> JPA ----------

    /** Maps the aggregate Order into a fresh JPA entity graph. */
    public JpaOrderEntity toEntity(Order order) {
        JpaOrderEntity e = new JpaOrderEntity();

        // Root fields
        e.setId(order.id() != null ? order.id().value() : null);
        e.setTenantId(order.tenantId().value());
        e.setCustomerId(order.customerId().value());
        e.setStatus(order.status());
        e.setTotalAmount(order.total().amount());
        e.setCurrency(order.total().currency());
        e.setCreatedAt(order.createdAt());
        e.setUpdatedAt(order.updatedAt());

        // Lines (maintain bidirectional association)
        List<JpaOrderLineEntity> lineEntities = new ArrayList<>(order.lines().size());
        for (OrderLine l : order.lines()) {
            JpaOrderLineEntity le = new JpaOrderLineEntity();
            le.setProductId(l.productId());
            le.setProductName(l.productName());
            le.setUnitPriceAmount(l.unitPrice().amount());
            le.setUnitPriceCurrency(l.unitPrice().currency());
            le.setQty(l.qty().value());
            le.setLineTotalAmount(l.lineTotal().amount());

            // attach to parent
            le.setOrder(e);
            lineEntities.add(le);
        }
        e.setLines(lineEntities);

        return e;
    }

    // ---------- JPA -> Domain ----------

    /** Rehydrates the aggregate from a JPA entity graph. */
    public Order toDomain(JpaOrderEntity e) {
        // Map lines first
        List<OrderLine> lines = new ArrayList<>(e.getLines().size());
        for (JpaOrderLineEntity le : e.getLines()) {
            lines.add(
                OrderLine.of(
                    le.getProductId(),
                    le.getProductName(),
                    Money.of(le.getUnitPriceAmount(), le.getUnitPriceCurrency()),
                    Quantity.of(le.getQty()),
                    Money.of(le.getLineTotalAmount(), le.getUnitPriceCurrency())
                )
            );
        }

        return Order.rehydrate(
                e.getId() != null ? OrderId.of(e.getId()) : null,
                TenantId.of(e.getTenantId()),
                CustomerId.of(e.getCustomerId()),
                lines,
                Money.of(e.getTotalAmount(), e.getCurrency()),
                e.getStatus(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
