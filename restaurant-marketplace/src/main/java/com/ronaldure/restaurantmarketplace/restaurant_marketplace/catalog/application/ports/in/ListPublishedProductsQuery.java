// .../ListPublishedProductsQuery.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query.ListPublishedProductsQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.PublicProductCardView;

public interface ListPublishedProductsQuery {
    PageResponse<PublicProductCardView> list(ListPublishedProductsQueryParams params);
}
