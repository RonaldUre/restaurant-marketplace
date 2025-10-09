package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out;

import java.util.Optional;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query.ListRestaurantsPlatformQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.PlatformRestaurantCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;

/**
 * OUT port para consultas de plataforma (SUPER_ADMIN) sobre restaurantes.
 * Debe usar proyecciones JPA (CQRS-light) y evitar rehidratación del agregado.
 */
public interface PlatformRestaurantQuery {

    PageResponse<PlatformRestaurantCardView> list(ListRestaurantsPlatformQueryParams params);

    Optional<RestaurantView> getById(Long id);
}
