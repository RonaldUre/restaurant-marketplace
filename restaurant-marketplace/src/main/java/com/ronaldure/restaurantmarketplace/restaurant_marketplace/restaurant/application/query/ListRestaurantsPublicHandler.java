// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/query/ListRestaurantsPublicHandler.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.ListRestaurantsPublicQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.PublicRestaurantQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListRestaurantsPublicHandler implements ListRestaurantsPublicQuery {

    private final PublicRestaurantQuery publicRestaurantQuery;

    public ListRestaurantsPublicHandler(PublicRestaurantQuery publicRestaurantQuery) {
        this.publicRestaurantQuery = publicRestaurantQuery;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RestaurantCardView> list(ListRestaurantsPublicQueryParams params) {
        PageRequest pageRequest = new PageRequest(params.page(), params.size());
        return publicRestaurantQuery.listPublic(pageRequest, params.cityFilter());
    }
}
