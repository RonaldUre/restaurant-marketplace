package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command.SwitchToLimitedCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.errors.InventoryItemNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.factory.InventoryFactory;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.mapper.InventoryApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.in.SwitchToLimitedUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out.CatalogOwnershipQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out.InventoryRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.view.InventoryAdminItemView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.InventoryItem;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Quantity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SwitchToLimitedService implements SwitchToLimitedUseCase {

    private final InventoryRepository repo;
    private final CatalogOwnershipQuery catalog;
    private final InventoryFactory factory;
    private final InventoryApplicationMapper mapper;
    private final CurrentTenantProvider tenantProvider;
    private final AccessControl accessControl;

    public SwitchToLimitedService(InventoryRepository repo,
                                  CatalogOwnershipQuery catalog,
                                  InventoryFactory factory,
                                  InventoryApplicationMapper mapper,
                                  CurrentTenantProvider tenantProvider,
                                  AccessControl accessControl) {
        this.repo = repo;
        this.catalog = catalog;
        this.factory = factory;
        this.mapper = mapper;
        this.tenantProvider = tenantProvider;
        this.accessControl = accessControl;
    }

    @Override
    @Transactional
    public InventoryAdminItemView switchToLimited(SwitchToLimitedCommand command) {
        accessControl.requireRole(Roles.RESTAURANT_ADMIN);
        TenantId tenantId = tenantProvider.requireCurrent();

        if (!catalog.belongsToTenant(tenantId, command.productId())) {
            throw new InventoryItemNotFoundException(command.productId());
        }

        // Si no existe, crea item limitado; si existe ilimitado, muta; si ya es limitado, es idempotente si initial>=reserved.
        InventoryItem result = repo.findByTenantAndProduct(tenantId, command.productId())
                .map(existing -> {
                    existing.switchToLimited(Quantity.of(command.initialAvailable()));
                    return repo.save(existing);
                })
                .orElseGet(() -> {
                    InventoryItem created = factory.createLimited(
                            tenantId,
                            com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.ProductId.of(command.productId()),
                            command);
                    return repo.save(created);
                });

        var basic = catalog.findProductBasic(tenantId, command.productId()).orElseThrow();
        return mapper.toAdminItemView(result, basic);
    }
}
