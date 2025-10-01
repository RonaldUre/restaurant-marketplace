package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.query.GetDailySalesQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view.DailySalesRow;

import java.util.List;

/**
 * Use case: obtener ventas diarias para un tenant en un rango de fechas.
 * Rango sugerido: [from, to] inclusivo (día calendario).
 */
public interface GetDailySalesQuery {
    List<DailySalesRow> execute(GetDailySalesQueryParams params);
}
