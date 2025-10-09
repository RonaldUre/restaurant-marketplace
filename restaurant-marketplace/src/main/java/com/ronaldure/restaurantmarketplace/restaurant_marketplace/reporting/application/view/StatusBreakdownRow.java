package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view;

/**
 * Distribución de pedidos por estado en un rango de fechas (tenant).
 * - status: "CREATED" | "PAID" | "CANCELLED".
 * - count: cantidad de pedidos con ese estado.
 */
public record StatusBreakdownRow(
        String status,
        long count
) {}
