package com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.domain.NotificationStatus;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.domain.NotificationType;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.entity.JpaNotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface NotificationJpaRepository extends JpaRepository<JpaNotificationEntity, Long> {

    Optional<JpaNotificationEntity> findByIdAndTenantId(Long id, Long tenantId);

    /**
     * Listado admin con filtros opcionales.
     * Orden recomendado en Pageable: createdAt DESC por defecto desde el controller.
     */
    @Query("""
           select n
           from JpaNotificationEntity n
           where n.tenantId = :tenantId
             and (:type is null or n.type = :type)
             and (:status is null or n.status = :status)
             and (:orderId is null or n.orderId = :orderId)
             and (:from is null or n.createdAt >= :from)
             and (:to   is null or n.createdAt <  :to)
           """)
    Page<JpaNotificationEntity> search(
            @Param("tenantId") Long tenantId,
            @Param("type") NotificationType type,
            @Param("status") NotificationStatus status,
            @Param("orderId") Long orderId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable
    );
}
