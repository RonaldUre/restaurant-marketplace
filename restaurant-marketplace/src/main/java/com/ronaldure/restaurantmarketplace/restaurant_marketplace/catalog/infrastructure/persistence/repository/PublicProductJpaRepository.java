// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/persistence/repository/PublicProductJpaRepository.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.entity.JpaProductEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.projection.PublicProductCardProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.projection.PublicProductDetailProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublicProductJpaRepository extends JpaRepository<JpaProductEntity, Long> {

    // Public detail: requires product published, restaurant OPEN, and not soft-deleted
    @Query("""
           select p
           from JpaProductEntity p
             join com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.entity.JpaRestaurantEntity r
               on r.id = p.tenantId
           where p.id = :productId
             and p.tenantId = :restaurantId
             and p.published = true
             and p.deletedAt is null
             and r.status = :openStatus
           """)
    Optional<PublicProductDetailProjection> findPublicDetail(@Param("restaurantId") Long restaurantId,
                                                             @Param("productId") Long productId,
                                                             @Param("openStatus") Status openStatus);

    // Public listing within a restaurant: published + restaurant OPEN + not soft-deleted
    @Query("""
           select p
           from JpaProductEntity p
             join com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.entity.JpaRestaurantEntity r
               on r.id = p.tenantId
           where p.tenantId = :restaurantId
             and p.published = true
             and p.deletedAt is null
             and r.status = :openStatus
             and (:category is null or p.category = :category)
             and (:q is null or :q = '' or lower(p.name) like lower(concat('%', :q, '%')))
           """)
    Page<PublicProductCardProjection> findPublicCards(@Param("restaurantId") Long restaurantId,
                                                      @Param("q") String q,
                                                      @Param("category") String category,
                                                      @Param("openStatus") Status openStatus,
                                                      Pageable pageable);
}
