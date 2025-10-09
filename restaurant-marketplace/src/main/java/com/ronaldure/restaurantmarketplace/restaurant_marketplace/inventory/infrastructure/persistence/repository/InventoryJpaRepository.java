// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/inventory/infrastructure/persistence/repository/InventoryJpaRepository.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.entity.JpaInventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryJpaRepository extends JpaRepository<JpaInventoryEntity, Long> {

    Optional<JpaInventoryEntity> findByTenantIdAndProductId(Long tenantId, Long productId);

    boolean existsByTenantIdAndProductId(Long tenantId, Long productId);
}
