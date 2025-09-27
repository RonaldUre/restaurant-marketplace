// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/inventory/infrastructure/persistence/repository/InventoryAdminJpaRepository.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.entity.JpaInventoryEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.projection.InventoryAdminItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryAdminJpaRepository extends JpaRepository<JpaInventoryEntity, Long> {

    /**
     * Admin listing for a tenant with optional filters:
     * - q: like filter on product name or sku (case-insensitive)
     * - category: exact match
     * - productId: exact match
     *
     * Only active (not soft-deleted) products are considered; inventory row may or may not exist.
     * We use INNER JOIN to show only items with inventory row; for "catalog-only" items without inventory,
     * listing can be handled via bootstrap or left out intentionally.
     */
    @Query("""
        select
            p.id as productId,
            p.sku as sku,
            p.name as name,
            p.category as category,
            i.available as available,
            i.reserved as reserved,
            i.createdAt as createdAt,
            i.updatedAt as updatedAt
        from JpaInventoryEntity i
        join com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.entity.JpaProductEntity p
             on p.id = i.productId
        where i.tenantId = :tenantId
          and p.tenantId = :tenantId
          and p.deletedAt is null
          and (:productId is null or p.id = :productId)
          and (:category is null or p.category = :category)
          and (:q is null or :q = '' or lower(p.name) like lower(concat('%', :q, '%')) or lower(p.sku) like lower(concat('%', :q, '%')))
        """)
    Page<InventoryAdminItemProjection> search(@Param("tenantId") Long tenantId,
                                              @Param("q") String q,
                                              @Param("category") String category,
                                              @Param("productId") Long productId,
                                              Pageable pageable);
}
