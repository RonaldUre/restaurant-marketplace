// .../UnpublishProductUseCase.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminDetailView;

public interface UnpublishProductUseCase {
    ProductAdminDetailView unpublish(Long productId);
}
