// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/persistence/repository/ProductJpaRepository.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.entity.JpaProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductJpaRepository extends JpaRepository<JpaProductEntity, Long> {

    // Command-side (tenant scoped + soft delete)
    Optional<JpaProductEntity> findByIdAndTenantIdAndDeletedAtIsNull(Long id, Long tenantId);

    // SKU uniqueness per tenant (DB has unique constraint too)
    boolean existsByTenantIdAndSkuIgnoreCaseAndDeletedAtIsNull(Long tenantId, String sku);
}
