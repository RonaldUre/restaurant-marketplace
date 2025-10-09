// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/persistence/adapter/AdminProductDetailQueryJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out.AdminProductDetailQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.projection.ProductAdminDetailProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.repository.AdminProductJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class AdminProductDetailQueryJpaAdapter implements AdminProductDetailQuery {

    private final AdminProductJpaRepository repo;

    public AdminProductDetailQueryJpaAdapter(AdminProductJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductAdminDetailView> findById(TenantId tenantId, Long productId) {
        return repo.findDetail(tenantId.value(), productId)
                   .map(this::toView);
    }

    private ProductAdminDetailView toView(ProductAdminDetailProjection p) {
        return new ProductAdminDetailView(
                p.getId(),
                p.getSku(),
                p.getName(),
                p.getDescription(),
                p.getCategory(),
                p.getPriceAmount(),
                p.getPriceCurrency(),
                p.isPublished(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
