package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.RestaurantNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.GetRestaurantPlatformQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.PlatformRestaurantQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetRestaurantPlatformHandler implements GetRestaurantPlatformQuery {

    private static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";

    private final PlatformRestaurantQuery platformQuery;
    private final AccessControl accessControl;

    public GetRestaurantPlatformHandler(PlatformRestaurantQuery platformQuery,
                                        AccessControl accessControl) {
        this.platformQuery = platformQuery;
        this.accessControl = accessControl;
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantView getById(Long id) {
        accessControl.requireRole(ROLE_SUPER_ADMIN);
        return platformQuery.getById(id).orElseThrow(() -> RestaurantNotFoundException.byId(id));
    }
}
