// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/persistence/mapper/OrderPersistenceMapper.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.Order;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.CustomerId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderLine;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.entity.JpaOrderEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.entity.JpaOrderLineEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Quantity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Mapper de persistencia para Order.
 *
 * Estrategia:
 *  - Creación: usar {@link #toEntityForCreate(Order)} para mapear raíz + líneas.
 *  - Actualización (p.ej. CREATED -> PAID): usar {@link #copyMutableFieldsExceptLines(JpaOrderEntity, Order)}
 *    para evitar borrar/reinsertar order_lines.
 */
@Component
public class OrderPersistenceMapper {

    // ---------- Domain -> JPA (CREACIÓN) ----------

    /** Mapea el agregado Order a una entidad JPA NUEVA (incluye líneas). */
    public JpaOrderEntity toEntityForCreate(Order order) {
        Objects.requireNonNull(order, "order is required");

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

        // Lines (bidireccional)
        List<JpaOrderLineEntity> lineEntities = new ArrayList<>(order.lines().size());
        for (OrderLine l : order.lines()) {
            JpaOrderLineEntity le = new JpaOrderLineEntity();
            le.setProductId(l.productId());
            le.setProductName(l.productName());
            le.setUnitPriceAmount(l.unitPrice().amount());
            le.setUnitPriceCurrency(l.unitPrice().currency());
            le.setQty(l.qty().value());
            le.setLineTotalAmount(l.lineTotal().amount());

            le.setOrder(e); // backref
            lineEntities.add(le);
        }
        e.setLines(lineEntities);

        return e;
    }

    // ---------- Domain -> JPA (ACTUALIZACIÓN SIN TOCAR LÍNEAS) ----------

    /**
     * Copia SOLO los campos mutables de la raíz (sin tocar la colección de líneas).
     * Úsalo cuando quieras, por ejemplo, marcar el pedido como PAID/CANCELLED
     * sin provocar DELETE/INSERT en order_lines.
     *
     * Nota: 'managed' debe ser una entidad gestionada por el EntityManager (cargada previamente).
     */
    public void copyMutableFieldsExceptLines(JpaOrderEntity managed, Order order) {
        Objects.requireNonNull(managed, "managed JPA entity is required");
        Objects.requireNonNull(order, "order is required");

        // Solo raíz mutable
        managed.setStatus(order.status());
        managed.setTotalAmount(order.total().amount());
        managed.setCurrency(order.total().currency());
        managed.setUpdatedAt(order.updatedAt());

        // NO tocar: managed.setLines(...)
        // La colección permanece intacta para evitar reinsertar/borrar.
    }

    // ---------- JPA -> Domain ----------

    /** Rehidrata el agregado desde una entidad JPA (incluye líneas). */
    public Order toDomain(JpaOrderEntity e) {
        Objects.requireNonNull(e, "entity is required");

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
