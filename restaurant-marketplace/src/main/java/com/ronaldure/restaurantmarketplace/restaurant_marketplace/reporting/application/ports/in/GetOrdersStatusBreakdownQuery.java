package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.query.GetOrdersStatusBreakdownQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view.StatusBreakdownRow;

import java.util.List;

/**
 * Use case: distribución de pedidos por estado (CREATED/PAID/CANCELLED)
 * en un rango de fechas para un tenant.
 */
public interface GetOrdersStatusBreakdownQuery {
    List<StatusBreakdownRow> execute(GetOrdersStatusBreakdownQueryParams params);
}
