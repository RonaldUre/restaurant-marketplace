package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.ListAllRestaurantsForSelectQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.PlatformRestaurantQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantForSelectView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;

@Service
public class ListAllRestaurantsForSelectHandler implements ListAllRestaurantsForSelectQuery {

    private final PlatformRestaurantQuery platformQuery;
    private final AccessControl accessControl;

    public ListAllRestaurantsForSelectHandler(PlatformRestaurantQuery platformQuery, AccessControl accessControl) {
        this.platformQuery = platformQuery;
        this.accessControl = accessControl;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantForSelectView> list() {
        accessControl.requireRole(Roles.SUPER_ADMIN);
        return platformQuery.listAllForSelect();
    }
}
