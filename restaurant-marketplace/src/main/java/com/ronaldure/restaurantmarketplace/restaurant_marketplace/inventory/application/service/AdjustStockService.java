package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command.AdjustStockCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.errors.InventoryItemNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.errors.InventoryOperationNotAllowedException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.mapper.InventoryApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.in.AdjustStockUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out.CatalogOwnershipQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out.InventoryRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.view.InventoryAdminItemView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.InventoryItem;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service
public class AdjustStockService implements AdjustStockUseCase {

    private final InventoryRepository repo;
    private final CatalogOwnershipQuery catalog;
    private final InventoryApplicationMapper mapper;
    private final CurrentTenantProvider tenantProvider;
    private final AccessControl accessControl;

    public AdjustStockService(InventoryRepository repo,
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
    public InventoryAdminItemView adjust(AdjustStockCommand command) {
        accessControl.requireRole(Roles.RESTAURANT_ADMIN);
        TenantId tenantId = tenantProvider.requireCurrent();

        if (!catalog.belongsToTenant(tenantId, command.productId())) {
            throw new InventoryItemNotFoundException(command.productId());
        }

        // Prefer atomic SQL if your adapter supports it:
        // if (repo.adjustAtomic(tenantId, command.productId(), command.delta())) { ...
        // }

        InventoryItem updated = withRetry(() -> {
            InventoryItem item = repo.findByTenantAndProduct(tenantId, command.productId())
                    .orElseThrow(() -> new InventoryItemNotFoundException(command.productId()));

            if (item.available() == null) {
                throw InventoryOperationNotAllowedException.cannotAdjustUnlimited(command.productId());
            }

            item.adjust(command.delta());

            return repo.save(item);
        });

        var basic = catalog.findProductBasic(tenantId, command.productId())
                .orElseThrow(() -> new InventoryItemNotFoundException(command.productId()));
        return mapper.toAdminItemView(updated, basic);
    }

    private InventoryItem withRetry(Supplier<InventoryItem> op) {
        int attempts = 0;
        while (true) {
            try {
                return op.get();
            } catch (OptimisticLockingFailureException ex) {
                if (++attempts >= 3)
                    throw ex;
            }
        }
    }
}
