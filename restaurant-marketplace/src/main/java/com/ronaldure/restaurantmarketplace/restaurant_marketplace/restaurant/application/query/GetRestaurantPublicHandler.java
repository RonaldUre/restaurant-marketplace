// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/query/GetRestaurantPublicHandler.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.RestaurantNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.GetRestaurantPublicQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.PublicRestaurantQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler de la consulta pública de detalle.
 * Política de error centralizada en application: 404 si no existe.
 */
@Service
public class GetRestaurantPublicHandler implements GetRestaurantPublicQuery {

    private final PublicRestaurantQuery publicRestaurantQuery;

    public GetRestaurantPublicHandler(PublicRestaurantQuery publicRestaurantQuery) {
        this.publicRestaurantQuery = publicRestaurantQuery;
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantView get(GetRestaurantPublicQueryParams params) {
        if (params == null || !params.hasExactlyOneTarget()) {
            throw new IllegalArgumentException("Exactly one target is required: id or slug");
        }

        if (params.id() != null) {
            Long id = params.id();
            return publicRestaurantQuery.getById(id)
                    .orElseThrow(() -> RestaurantNotFoundException.byId(id));
        } else {
            String slug = params.slug();
            return publicRestaurantQuery.getBySlug(slug)
                    .orElseThrow(() -> RestaurantNotFoundException.bySlug(slug));
        }
    }
}
