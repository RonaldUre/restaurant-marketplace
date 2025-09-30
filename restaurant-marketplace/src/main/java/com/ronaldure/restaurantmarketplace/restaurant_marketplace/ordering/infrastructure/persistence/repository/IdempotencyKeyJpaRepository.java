// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/persistence/repository/IdempotencyKeyJpaRepository.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.entity.JpaIdempotencyKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyKeyJpaRepository extends JpaRepository<JpaIdempotencyKeyEntity, Long> {

    Optional<JpaIdempotencyKeyEntity> findByTenantIdAndCustomerIdAndIdempotencyKey(
            Long tenantId, Long customerId, String idempotencyKey
    );

    boolean existsByTenantIdAndCustomerIdAndIdempotencyKey(
            Long tenantId, Long customerId, String idempotencyKey
    );
}
