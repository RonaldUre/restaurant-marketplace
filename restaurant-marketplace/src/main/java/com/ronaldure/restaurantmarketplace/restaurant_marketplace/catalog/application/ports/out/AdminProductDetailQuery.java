package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;

import java.util.Optional;

public interface AdminProductDetailQuery {
    Optional<ProductAdminDetailView> findById(TenantId tenantId, Long productId);
}
