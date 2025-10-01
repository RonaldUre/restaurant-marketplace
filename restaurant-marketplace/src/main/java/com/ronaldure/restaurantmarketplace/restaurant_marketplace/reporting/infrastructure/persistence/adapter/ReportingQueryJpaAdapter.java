package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.ports.out.ReportingQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view.DailySalesRow;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view.StatusBreakdownRow;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view.TopProductRow;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.persistence.projection.DailySalesProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.persistence.projection.StatusBreakdownProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.persistence.projection.TopProductProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.persistence.repository.ReportingJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA adapter that implements ReportingQuery using native SQL interface projections.
 * - Converts [from..to] (LocalDate) into [from@00:00, to+1@00:00) timestamps.
 * - Maps DB projections -> application views.
 */
@Repository
@Transactional(readOnly = true)
public class ReportingQueryJpaAdapter implements ReportingQuery {

    private final ReportingJpaRepository repo;

    public ReportingQueryJpaAdapter(ReportingJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<DailySalesRow> dailySales(Long tenantId, LocalDate from, LocalDate to) {
        var bounds = toBounds(from, to);
        List<DailySalesProjection> rows = repo.dailySales(
                tenantId, bounds.fromInclusive(), bounds.toExclusive()
        );
        return rows.stream()
                .map(r -> new DailySalesRow(
                        r.getDate(),
                        r.getOrders() == null ? 0L : r.getOrders(),
                        r.getTotalAmount(),
                        r.getCurrency()
                ))
                .toList();
    }

    @Override
    public List<TopProductRow> topProducts(Long tenantId, LocalDate from, LocalDate to, int limit) {
        var bounds = toBounds(from, to);
        // Handler already normalizes limit, but guard just in case:
        int safeLimit = (limit <= 0) ? 10 : limit;
        var pageable = PageRequest.of(0, safeLimit);

        List<TopProductProjection> rows = repo.topProducts(
                tenantId, bounds.fromInclusive(), bounds.toExclusive(), pageable
        );
        return rows.stream()
                .map(r -> new TopProductRow(
                        r.getProductId() == null ? 0L : r.getProductId(),
                        r.getName(),
                        r.getQty() == null ? 0L : r.getQty(),
                        r.getRevenue(),
                        r.getCurrency()
                ))
                .toList();
    }

    @Override
    public List<StatusBreakdownRow> ordersByStatus(Long tenantId, LocalDate from, LocalDate to) {
        var bounds = toBounds(from, to);
        List<StatusBreakdownProjection> rows = repo.ordersByStatus(
                tenantId, bounds.fromInclusive(), bounds.toExclusive()
        );
        return rows.stream()
                .map(r -> new StatusBreakdownRow(
                        r.getStatus(),
                        r.getCount() == null ? 0L : r.getCount()
                ))
                .toList();
    }

    // ---- helpers ----

    private static Range toBounds(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from/to are required");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("to must be >= from");
        }
        LocalDateTime fromTs = from.atStartOfDay();
        LocalDateTime toExclusive = to.plusDays(1).atStartOfDay();
        return new Range(fromTs, toExclusive);
    }

    private record Range(LocalDateTime fromInclusive, LocalDateTime toExclusive) {}
}
