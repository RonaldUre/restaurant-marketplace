// .../ListProductsAdminQuery.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query.ListProductsAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminCardView;

public interface ListProductsAdminQuery {
    PageResponse<ProductAdminCardView> list(ListProductsAdminQueryParams params);
}
