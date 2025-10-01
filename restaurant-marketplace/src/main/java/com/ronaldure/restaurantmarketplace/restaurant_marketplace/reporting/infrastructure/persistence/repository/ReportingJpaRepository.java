package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.persistence.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.entity.JpaOrderEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.persistence.projection.DailySalesProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.persistence.projection.StatusBreakdownProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.persistence.projection.TopProductProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Reporting repository (read-only) con native SQL + interface projections.
 * No usa paginación; el límite (Top-N) se controla por parámetro.
 */
@Repository
@Transactional(readOnly = true)
public interface ReportingJpaRepository extends JpaRepository<JpaOrderEntity, Long> {

    @Query(value = """
            SELECT
                DATE(o.created_at)       AS date,
                COUNT(*)                 AS orders,
                SUM(o.total_amount)      AS totalAmount,
                MAX(o.currency)          AS currency
            FROM orders o
            WHERE o.tenant_id = :tenantId
              AND o.created_at >= :fromTs
              AND o.created_at <  :toTs
              AND o.status = 'PAID'
            GROUP BY DATE(o.created_at)
            ORDER BY date ASC
            """, nativeQuery = true)
    List<DailySalesProjection> dailySales(
            @Param("tenantId") Long tenantId,
            @Param("fromTs") LocalDateTime fromInclusive,
            @Param("toTs") LocalDateTime toExclusive);

    @Query(value = """
            SELECT
                ol.product_id              AS productId,
                MAX(ol.product_name)       AS name,
                SUM(ol.qty)                AS qty,
                SUM(ol.line_total_amount)  AS revenue,
                MAX(o.currency)            AS currency
            FROM order_lines ol
            JOIN orders o ON o.id = ol.order_id
            WHERE o.tenant_id = :tenantId
              AND o.created_at >= :fromTs
              AND o.created_at <  :toTs
              AND o.status = 'PAID'
            GROUP BY ol.product_id
            ORDER BY revenue DESC
            """, nativeQuery = true)
    List<TopProductProjection> topProducts(
            @Param("tenantId") Long tenantId,
            @Param("fromTs") LocalDateTime fromInclusive,
            @Param("toTs") LocalDateTime toExclusive,
            org.springframework.data.domain.Pageable pageable);

    @Query(value = """
            SELECT
                o.status  AS status,
                COUNT(*)  AS count
            FROM orders o
            WHERE o.tenant_id = :tenantId
              AND o.created_at >= :fromTs
              AND o.created_at <  :toTs
            GROUP BY o.status
            ORDER BY count DESC
            """, nativeQuery = true)
    List<StatusBreakdownProjection> ordersByStatus(
            @Param("tenantId") Long tenantId,
            @Param("fromTs") LocalDateTime fromInclusive,
            @Param("toTs") LocalDateTime toExclusive);
}
