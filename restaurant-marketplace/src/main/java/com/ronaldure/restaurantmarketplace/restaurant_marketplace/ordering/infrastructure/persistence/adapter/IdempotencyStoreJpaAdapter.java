// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/persistence/adapter/IdempotencyStoreJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors.DuplicateIdempotencyKeyException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.IdempotencyStore;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.entity.JpaIdempotencyKeyEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.repository.IdempotencyKeyJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class IdempotencyStoreJpaAdapter implements IdempotencyStore {

    private final IdempotencyKeyJpaRepository jpa;

    public IdempotencyStoreJpaAdapter(IdempotencyKeyJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Existing> find(TenantId tenantId, UserId customerId, String key) {
        return jpa.findByTenantIdAndCustomerIdAndIdempotencyKey(
                        tenantId.value(), Long.parseLong(customerId.value()), key)
                .map(e -> new Existing(e.getIdempotencyKey(), e.getOrderId()));
    }

    @Override
    @Transactional
    public void save(TenantId tenantId, UserId customerId, String key, long orderId) {
        try {
            JpaIdempotencyKeyEntity e = new JpaIdempotencyKeyEntity();
            e.setTenantId(tenantId.value());
            e.setCustomerId(Long.parseLong(customerId.value()));
            e.setIdempotencyKey(key);
            e.setOrderId(orderId);
            jpa.save(e);
        } catch (DataIntegrityViolationException ex) {
            // Colisión UNIQUE(tenant_id, customer_id, idempotency_key)
            throw new DuplicateIdempotencyKeyException(key);
        }
    }
}
