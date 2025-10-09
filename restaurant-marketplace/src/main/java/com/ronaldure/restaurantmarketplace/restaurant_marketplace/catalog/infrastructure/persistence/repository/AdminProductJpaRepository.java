// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/persistence/repository/AdminProductJpaRepository.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.entity.JpaProductEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.projection.ProductAdminCardProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.projection.ProductAdminDetailProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminProductJpaRepository extends JpaRepository<JpaProductEntity, Long> {

    // Detail (tenant scoped + soft delete)
    @Query("""
           select p
           from JpaProductEntity p
           where p.id = :id
             and p.tenantId = :tenantId
             and p.deletedAt is null
           """)
    Optional<ProductAdminDetailProjection> findDetail(@Param("tenantId") Long tenantId,
                                                     @Param("id") Long id);

    // Listing base (tenant scoped + soft delete)
    @Query("""
           select p
           from JpaProductEntity p
           where p.tenantId = :tenantId
             and p.deletedAt is null
           """)
    Page<ProductAdminCardProjection> findCards(@Param("tenantId") Long tenantId, Pageable pageable);

    // Listing with optional filters (q by name/sku, category, published)
    @Query("""
           select p
           from JpaProductEntity p
           where p.tenantId = :tenantId
             and p.deletedAt is null
             and (:published is null or p.published = :published)
             and (:category is null or p.category = :category)
             and (:q is null or :q = '' or
                  lower(p.name) like lower(concat('%', :q, '%')) or
                  lower(p.sku)  like lower(concat('%', :q, '%')))
           """)
    Page<ProductAdminCardProjection> search(@Param("tenantId") Long tenantId,
                                            @Param("q") String q,
                                            @Param("category") String category,
                                            @Param("published") Boolean published,
                                            Pageable pageable);
}
