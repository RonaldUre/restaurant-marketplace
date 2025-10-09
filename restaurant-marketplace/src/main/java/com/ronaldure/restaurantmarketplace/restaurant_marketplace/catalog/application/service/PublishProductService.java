package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.errors.ProductNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.mapper.ProductApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in.PublishProductUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out.ProductRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.Product;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.ProductId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublishProductService implements PublishProductUseCase {

    private final ProductRepository productRepository;
    private final CurrentTenantProvider tenantProvider;
    private final AccessControl accessControl;
    private final ProductApplicationMapper mapper;


    public PublishProductService(ProductRepository productRepository,
                                 CurrentTenantProvider tenantProvider,
                                 AccessControl accessControl,
                                 ProductApplicationMapper mapper) {
        this.productRepository = productRepository;
        this.tenantProvider = tenantProvider;
        this.accessControl = accessControl;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ProductAdminDetailView publish(Long productId) {
        // 1) Authorization
        accessControl.requireRole(Roles.RESTAURANT_ADMIN);

        // 2) Tenant from JWT
        TenantId tenantId = tenantProvider.requireCurrent();

        // 3) Load aggregate (scoped by tenant)
        Product product = productRepository
                .findById(ProductId.of(productId), tenantId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // 4) Domain rule
        product.publish();

        // 5) Persist
        Product saved = productRepository.save(product);

        return mapper.toAdminDetail(saved);
    }
}

