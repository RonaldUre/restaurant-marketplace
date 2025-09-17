package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query.ListRestaurantsPlatformQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.PlatformRestaurantCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;

/**
 * Platform query (SUPER_ADMIN) para listar restaurantes en cualquier estado.
 * No rehidrata el agregado; debe apoyarse en proyecciones (CQRS-light).
 */
public interface ListRestaurantsPlatformQuery {

    /**
     * Lista con filtros y ordenamiento.
     * @param params parámetros de paginación, filtros y orden.
     * @return PageResponse con items y metadatos.
     */
    PageResponse<PlatformRestaurantCardView> list(ListRestaurantsPlatformQueryParams params);
}
