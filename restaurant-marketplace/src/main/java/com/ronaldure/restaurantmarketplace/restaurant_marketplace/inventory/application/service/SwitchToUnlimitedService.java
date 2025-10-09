package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command.SwitchToUnlimitedCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.errors.InventoryItemNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.errors.InventoryOperationNotAllowedException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.mapper.InventoryApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.in.SwitchToUnlimitedUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out.CatalogOwnershipQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out.InventoryRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.view.InventoryAdminItemView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.InventoryItem;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SwitchToUnlimitedService implements SwitchToUnlimitedUseCase {

    private final InventoryRepository repo;
    private final CatalogOwnershipQuery catalog;
    private final InventoryApplicationMapper mapper;
    private final CurrentTenantProvider tenantProvider;
    private final AccessControl accessControl;

    public SwitchToUnlimitedService(InventoryRepository repo,
            CatalogOwnershipQuery catalog,
            InventoryApplicationMapper mapper,
            CurrentTenantProvider tenantProvider,
            AccessControl accessControl) {
        this.repo = repo;
        this.catalog = catalog;
        this.mapper = mapper;
        this.tenantProvider = tenantProvider;
        this.accessControl = accessControl;
    }

    @Override
    @Transactional
    public InventoryAdminItemView switchToUnlimited(SwitchToUnlimitedCommand command) {
        accessControl.requireRole(Roles.RESTAURANT_ADMIN);
        TenantId tenantId = tenantProvider.requireCurrent();

        if (!catalog.belongsToTenant(tenantId, command.productId())) {
            throw new InventoryItemNotFoundException(command.productId());
        }

        InventoryItem item = repo.findByTenantAndProduct(tenantId, command.productId())
                .orElseThrow(() -> new InventoryItemNotFoundException(command.productId()));

        if (item.reserved().value() > 0) {
            throw InventoryOperationNotAllowedException.cannotSwitchToUnlimitedWithReservations(command.productId());
        }

        item.switchToUnlimited(); // throws if reserved > 0
        InventoryItem saved = repo.save(item);

        var basic = catalog.findProductBasic(tenantId, command.productId()).orElseThrow();
        return mapper.toAdminItemView(saved, basic);
    }
}
