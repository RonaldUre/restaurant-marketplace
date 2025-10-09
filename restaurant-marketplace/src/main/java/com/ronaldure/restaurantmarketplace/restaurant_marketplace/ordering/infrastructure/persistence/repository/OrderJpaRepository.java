// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/persistence/repository/OrderJpaRepository.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderStatus;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.entity.JpaOrderEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.projection.OrderAdminCardProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.projection.OrderAdminDetailProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.projection.OrderBasicProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.projection.OrderPublicDetailProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<JpaOrderEntity, Long> {

    // ------- Commands (aggregate) -------

    /** Load aggregate by id + tenant for write use-cases. */
    Optional<JpaOrderEntity> findByIdAndTenantId(Long id, Long tenantId);

    // ------- Read models (projections) -------

    /** Admin detail (owner tenant) via interface-based projection. */
    Optional<OrderAdminDetailProjection> findProjectedByIdAndTenantId(Long id, Long tenantId);

    /** Public detail (must be owned by the customer) via projection. */
    Optional<OrderPublicDetailProjection> findProjectedByIdAndCustomerId(Long id, Long customerId);

    /**
     * Admin list with optional filters and computed itemsCount (SUM of quantities).
     * Returns lightweight cards via projection.
     */
    @Query("""
           select 
              o.id as id,
              o.status as status,
              o.totalAmount as totalAmount,
              o.currency as currency,
              coalesce(sum(l.qty),0) as itemsCount,
              o.createdAt as createdAt
           from JpaOrderEntity o
           left join o.lines l
           where o.tenantId = :tenantId
             and (:status is null or o.status = :status)
             and (:customerId is null or o.customerId = :customerId)
             and (:createdFrom is null or o.createdAt >= :createdFrom)
             and (:createdTo   is null or o.createdAt <  :createdTo)
           group by o.id, o.status, o.totalAmount, o.currency, o.createdAt
           """)
    Page<OrderAdminCardProjection> searchAdminCards(
            @Param("tenantId") Long tenantId,
            @Param("status") OrderStatus status,
            @Param("customerId") Long customerId,
            @Param("createdFrom") Instant createdFrom,
            @Param("createdTo") Instant createdTo,
            Pageable pageable
    );

    Optional<OrderBasicProjection> findProjectedById(Long id);
}
