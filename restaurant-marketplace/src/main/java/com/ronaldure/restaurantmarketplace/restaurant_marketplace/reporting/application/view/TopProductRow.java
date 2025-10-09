package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view;

import java.math.BigDecimal;

/**
 * Producto agregado por ventas en un rango de fechas (tenant).
 * - productId: identificador del producto.
 * - name: snapshot del nombre usado al momento de la venta (o el actual si agregas por líneas).
 * - qty: cantidad total vendida (sumatoria de líneas).
 * - revenue: ingreso total generado por el producto en el rango.
 * - currency: ISO-4217.
 */
public record TopProductRow( 
        long productId,
        String name,
        long qty,
        BigDecimal revenue,
        String currency
) {}
