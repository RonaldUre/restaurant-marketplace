package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.query.GetTopProductsQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view.TopProductRow;

import java.util.List;

/**
 * Use case: obtener el Top-N de productos por revenue/cantidad
 * en un rango de fechas para un tenant.
 */
public interface GetTopProductsQuery {
    List<TopProductRow> execute(GetTopProductsQueryParams params);
}
