// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/persistence/repository/CatalogReadJpaRepository.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.entity.JpaProductEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.projection.ProductBasicProjection;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatalogReadJpaRepository extends JpaRepository<JpaProductEntity, Long> {

    /** Ownership check: product belongs to tenant and not soft-deleted. */
    boolean existsByIdAndTenantIdAndDeletedAtIsNull(Long id, Long tenantId);

    /** Minimal basics for admin views. */
    @Query("""
        select
            p.id as productId,
            p.sku as sku,
            p.name as name,
            p.category as category
        from JpaProductEntity p
        where p.id = :productId
          and p.tenantId = :tenantId
          and p.deletedAt is null
        """)
    Optional<ProductBasicProjection> findProductBasic(@Param("tenantId") Long tenantId,
                                                      @Param("productId") Long productId);
}
