// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/persistence/adapter/OrderRepositoryJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.OrderRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.Order;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.entity.JpaOrderEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.mapper.OrderPersistenceMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.repository.OrderJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class OrderRepositoryJpaAdapter implements OrderRepository {

    private final OrderJpaRepository jpa;
    private final OrderPersistenceMapper mapper;

    public OrderRepositoryJpaAdapter(OrderJpaRepository jpa, OrderPersistenceMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Order save(Order order) {
        // CREATE (sin id): mapear raíz + líneas
        if (order.id() == null) {
            JpaOrderEntity e = mapper.toEntityForCreate(order);
            JpaOrderEntity saved = jpa.save(e);

            // Propagar id generado al agregado
            if (saved.getId() != null) {
                order.assignId(OrderId.of(saved.getId()));
            }
            return mapper.toDomain(saved);
        }

        // UPDATE (con id): cargar entidad gestionada y copiar solo campos mutables de
        // la raíz
        // Tip: si quieres blindar por tenant, usa findByIdAndTenantId(...)
        JpaOrderEntity managed = jpa.findByIdAndTenantId(order.id().value(), order.tenantId().value())
                .orElseThrow(() -> new IllegalStateException(
                        "Order not found id=" + order.id().value() + " tenant=" + order.tenantId().value()));

        // No tocar la colección de lines para evitar DELETE/INSERT en TX B
        mapper.copyMutableFieldsExceptLines(managed, order);

        JpaOrderEntity saved = jpa.save(managed);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(OrderId id, TenantId tenantId) {
        return jpa.findByIdAndTenantId(id.value(), tenantId.value()).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(OrderId id) {
        // Usa el findById(PK) que viene por defecto en JpaRepository
        return jpa.findById(id.value()).map(mapper::toDomain);
    }
}
