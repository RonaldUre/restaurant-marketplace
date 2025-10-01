package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view.DailySalesRow;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view.TopProductRow;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view.StatusBreakdownRow;

import java.time.LocalDate;
import java.util.List;

/**
 * Puerto de lectura (CQRS) para Reporting.
 * Implementación típica: adapter JPA con SQL nativo.
 *
 * Convención de rango de fechas:
 * - Interpretar [from, to] como días calendario inclusivos.
 * - La implementación puede aplicar from >= 00:00 y to < (to+1) 00:00.
 */
public interface ReportingQuery {

    /**
     * Ventas diarias (solo PAID) para un tenant.
     */
    List<DailySalesRow> dailySales(Long tenantId, LocalDate from, LocalDate to);

    /**
     * Top-N productos por revenue (y qty) para un tenant.
     */
    List<TopProductRow> topProducts(Long tenantId, LocalDate from, LocalDate to, int limit);

    /**
     * Distribución de pedidos por estado para un tenant.
     */
    List<StatusBreakdownRow> ordersByStatus(Long tenantId, LocalDate from, LocalDate to);
}
