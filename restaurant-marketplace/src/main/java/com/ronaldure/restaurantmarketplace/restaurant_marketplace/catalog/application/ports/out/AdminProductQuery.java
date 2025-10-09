// .../ports/out/AdminProductQuery.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query.ListProductsAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;

public interface AdminProductQuery {
    PageResponse<ProductAdminCardView> list(TenantId tenantId, ListProductsAdminQueryParams params, PageRequest page);
}
