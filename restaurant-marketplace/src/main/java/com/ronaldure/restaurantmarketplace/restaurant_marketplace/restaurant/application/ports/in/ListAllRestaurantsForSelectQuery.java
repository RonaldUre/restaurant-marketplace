package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in;

import java.util.List;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantForSelectView;

/** Platform query (SUPER_ADMIN) to fetch all restaurants {id, name} unpaged. */
public interface ListAllRestaurantsForSelectQuery {
    List<RestaurantForSelectView> list();
}
