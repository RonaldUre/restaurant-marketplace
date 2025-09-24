// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/application/service/UpdateProductService.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.command.UpdateProductCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.errors.ProductNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.factory.ProductFactory;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.mapper.ProductApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in.UpdateProductUseCase;
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
public class UpdateProductService implements UpdateProductUseCase {

    private final ProductRepository productRepository;
    private final ProductFactory productFactory;
    private final ProductApplicationMapper mapper;
    private final CurrentTenantProvider tenantProvider;
    private final AccessControl accessControl;

    public UpdateProductService(ProductRepository productRepository,
                                ProductFactory productFactory,
                                ProductApplicationMapper mapper,
                                CurrentTenantProvider tenantProvider,
                                AccessControl accessControl) {
        this.productRepository = productRepository;
        this.productFactory = productFactory;
        this.mapper = mapper;
        this.tenantProvider = tenantProvider;
        this.accessControl = accessControl;
    }

    @Override
    @Transactional
    public ProductAdminDetailView update(UpdateProductCommand command) {
        // 1) Authorization
        accessControl.requireRole(Roles.RESTAURANT_ADMIN);

        // 2) Tenant from JWT
        TenantId tenantId = tenantProvider.requireCurrent();

        // 3) Load aggregate (scoped by tenant)
        Product product = productRepository
                .findById(ProductId.of(command.productId()), tenantId)
                .orElseThrow(() -> new ProductNotFoundException(command.productId()));

        // 4) Build payload and mutate aggregate
        var payload = productFactory.toUpdatePayload(command);
        product.updateDetails(payload.name(), payload.description(), payload.category(), payload.price());

        // 5) Persist & map
        Product saved = productRepository.save(product);
        return mapper.toAdminDetail(saved);
    }
}
