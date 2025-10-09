// .../ports/out/ProductRepository.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.Product;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.ProductId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.Sku;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;

import java.util.Optional;

public interface ProductRepository {
    // Commands (admin, always tenant-scoped)
    Product save(Product product);
    Optional<Product> findById(ProductId id, TenantId tenantId);
    boolean existsByTenantAndSku(TenantId tenantId, Sku sku);
}
