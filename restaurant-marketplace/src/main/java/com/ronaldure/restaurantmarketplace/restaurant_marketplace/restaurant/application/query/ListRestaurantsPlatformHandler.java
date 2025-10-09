package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.ListRestaurantsPlatformQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.PlatformRestaurantQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.PlatformRestaurantCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler de la consulta de plataforma. Exige SUPER_ADMIN y delega en el OUT port
 * de lectura optimizada (proyecciones).
 */
@Service
public class ListRestaurantsPlatformHandler implements ListRestaurantsPlatformQuery {

    private final PlatformRestaurantQuery platformQuery;
    private final AccessControl accessControl;

    public ListRestaurantsPlatformHandler(PlatformRestaurantQuery platformQuery,
                                          AccessControl accessControl) {
        this.platformQuery = platformQuery;
        this.accessControl = accessControl;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PlatformRestaurantCardView> list(ListRestaurantsPlatformQueryParams params) {
        accessControl.requireRole(Roles.SUPER_ADMIN);
        return platformQuery.list(params);
    }
}
