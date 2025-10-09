package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.errors.ProductNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in.GetPublicProductQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out.PublicProductDetailQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.PublicProductDetailView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetPublicProductHandler implements GetPublicProductQuery {

    private final PublicProductDetailQuery publicDetailQuery;

    public GetPublicProductHandler(PublicProductDetailQuery publicDetailQuery) {
        this.publicDetailQuery = publicDetailQuery;
    }

    @Override
    @Transactional(readOnly = true)
    public PublicProductDetailView get(Long restaurantId, Long productId) {
        // Public endpoint: adapter must enforce published=true and restaurant.status=OPEN
        return publicDetailQuery
                .findByRestaurantAndId(restaurantId, productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}
