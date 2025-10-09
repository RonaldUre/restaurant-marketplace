// .../ports/out/PublicCatalogQuery.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query.ListPublishedProductsQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.PublicProductCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;

public interface PublicCatalogQuery {
    PageResponse<PublicProductCardView> listPublished(ListPublishedProductsQueryParams params, PageRequest page);
}
