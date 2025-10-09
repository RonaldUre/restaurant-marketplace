// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/application/query/ListPublishedProductsHandler.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in.ListPublishedProductsQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out.PublicCatalogQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.PublicProductCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListPublishedProductsHandler implements ListPublishedProductsQuery {

    private final PublicCatalogQuery publicCatalogQuery;

    public ListPublishedProductsHandler(PublicCatalogQuery publicCatalogQuery) {
        this.publicCatalogQuery = publicCatalogQuery;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PublicProductCardView> list(ListPublishedProductsQueryParams params) {
        // Build PageRequest inside the handler
        PageRequest pageRequest = new PageRequest(params.page(), params.size());

        return publicCatalogQuery.listPublished(params, pageRequest);
    }
}
