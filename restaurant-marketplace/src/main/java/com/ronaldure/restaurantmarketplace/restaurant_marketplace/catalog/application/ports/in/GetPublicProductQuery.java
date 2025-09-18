// .../application/ports/in/GetPublicProductQuery.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.PublicProductDetailView;

public interface GetPublicProductQuery {
    PublicProductDetailView  get(Long restaurantId, Long productId);
}
