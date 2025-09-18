// src/main/java/.../catalog/application/service/CreateProductService.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.command.CreateProductCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.errors.SkuAlreadyInUseException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.factory.ProductFactory;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.mapper.ProductApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in.CreateProductUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out.ProductRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.Product;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.Sku;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductService implements CreateProductUseCase {

    private final ProductRepository productRepository;
    private final ProductFactory productFactory;
    private final ProductApplicationMapper mapper;
    private final CurrentTenantProvider tenantProvider; 

    public CreateProductService(ProductRepository productRepository,
                                ProductFactory productFactory,
                                ProductApplicationMapper mapper,
                                CurrentTenantProvider tenantProvider) {
        this.productRepository = productRepository;
        this.productFactory = productFactory;
        this.mapper = mapper;
        this.tenantProvider = tenantProvider;
    }

    @Override
    @Transactional
    public ProductAdminDetailView create(CreateProductCommand command) {
        // Resolve tenant from JWT (never from request)
        TenantId tenantId = tenantProvider.requireCurrent();

        // Enforce uniqueness by (tenant, sku)
        if (productRepository.existsByTenantAndSku(tenantId, Sku.of(command.sku()))) {
            throw new SkuAlreadyInUseException(command.sku());
        }

        // Build aggregate and persist
        Product product = productFactory.createNew(tenantId, command);
        Product saved = productRepository.save(product);

        // Map to admin detail view
        return mapper.toAdminDetail(saved);
    }
}
