package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.errors.ProductNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in.UnpublishProductUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out.ProductRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.Product;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.ProductId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UnpublishProductService implements UnpublishProductUseCase {

    private final ProductRepository productRepository;
    private final CurrentTenantProvider tenantProvider;
    private final AccessControl accessControl;

    public UnpublishProductService(ProductRepository productRepository,
                                   CurrentTenantProvider tenantProvider,
                                   AccessControl accessControl) {
        this.productRepository = productRepository;
        this.tenantProvider = tenantProvider;
        this.accessControl = accessControl;
    }

    @Override
    @Transactional
    public void unpublish(Long productId) {
        // 1) Authorization
        accessControl.requireRole("RESTAURANT_ADMIN");

        // 2) Tenant from JWT
        TenantId tenantId = tenantProvider.requireCurrent();

        // 3) Load aggregate (scoped by tenant)
        Product product = productRepository
                .findById(ProductId.of(productId), tenantId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // 4) Domain rule
        product.unpublish();

        // 5) Persist
        productRepository.save(product);
    }
}
