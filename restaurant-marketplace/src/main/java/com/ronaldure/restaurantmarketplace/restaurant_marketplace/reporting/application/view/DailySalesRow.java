package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Fila de reporte de ventas diarias por tenant.
 * - date: día calendario (en la zona horaria definida por la app/UI).
 * - orders: cantidad de pedidos "PAID" del día.
 * - totalAmount: suma del total de los pedidos del día.
 * - currency: ISO-4217 de la moneda (ej. "USD", "EUR").
 */
public record DailySalesRow(
        LocalDate date,
        long orders,
        BigDecimal totalAmount,
        String currency
) {}