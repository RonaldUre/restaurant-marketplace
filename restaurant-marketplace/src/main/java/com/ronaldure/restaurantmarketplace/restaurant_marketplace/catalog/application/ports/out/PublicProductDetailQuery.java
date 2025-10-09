package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.PublicProductDetailView;

import java.util.Optional;

public interface PublicProductDetailQuery {
    Optional<PublicProductDetailView> findByRestaurantAndId(Long restaurantId, Long productId);
}
