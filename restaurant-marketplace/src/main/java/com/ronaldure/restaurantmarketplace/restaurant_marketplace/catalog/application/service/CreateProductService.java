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
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.events.DomainEventPublisher;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.events.ProductCreatedEvent;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductService implements CreateProductUseCase {

    private final ProductRepository productRepository;
    private final ProductFactory productFactory;
    private final DomainEventPublisher events;
    private final ProductApplicationMapper mapper;
    private final CurrentTenantProvider tenantProvider;
    private final AccessControl accessControl;

    public CreateProductService(ProductRepository productRepository,
            ProductFactory productFactory,
            ProductApplicationMapper mapper,
            CurrentTenantProvider tenantProvider,
            AccessControl accessControl,
            DomainEventPublisher events) {
        this.productRepository = productRepository;
        this.productFactory = productFactory;
        this.mapper = mapper;
        this.tenantProvider = tenantProvider;
        this.accessControl = accessControl;
        this.events = events;
    }

    @Override
    @Transactional
    public ProductAdminDetailView create(CreateProductCommand command) {
        // 1) Authorization guard
        accessControl.requireRole(Roles.RESTAURANT_ADMIN);

        // 2) Tenant resolution from JWT (never from request)
        TenantId tenantId = tenantProvider.requireCurrent();

        // 3) Enforce uniqueness by (tenant, sku)
        if (productRepository.existsByTenantAndSku(tenantId, Sku.of(command.sku()))) {
            throw new SkuAlreadyInUseException(command.sku());
        }

        // 4) Build aggregate and persist
        Product product = productFactory.createNew(tenantId, command);
        Product saved = productRepository.save(product);

        // 5) Publish event que sera consumido en inventory para crearlo
        events.publish(ProductCreatedEvent.of(saved.tenantId(), saved.id()));

        // 5) Map to admin detail view
        return mapper.toAdminDetail(saved);
    }
}
