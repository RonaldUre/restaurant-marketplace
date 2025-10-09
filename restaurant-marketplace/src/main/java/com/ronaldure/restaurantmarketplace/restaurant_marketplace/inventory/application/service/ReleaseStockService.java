package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command.ReleaseStockCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.errors.InventoryItemNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.in.ReleaseStockUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out.InventoryRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.InventoryItem;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Quantity;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BooleanSupplier;

@Service
public class ReleaseStockService implements ReleaseStockUseCase {

    private final InventoryRepository repo;
    private final CurrentTenantProvider tenantProvider;

    public ReleaseStockService(InventoryRepository repo,
                               CurrentTenantProvider tenantProvider) {
        this.repo = repo;
        this.tenantProvider = tenantProvider;
    }

    @Override
    @Transactional
    public void release(ReleaseStockCommand command) {
        TenantId tenantId = tenantProvider.requireCurrent();

        // if (!repo.releaseAtomic(tenantId, command.productId(), command.quantity())) { /* idempotente */ }

        withRetryVoid(() -> {
            InventoryItem item = repo.findByTenantAndProduct(tenantId, command.productId())
                    .orElseThrow(() -> new InventoryItemNotFoundException(command.productId()));
            // Domain en ilimitado hace no-op; en limitado valida reserved>=qty
            item.release(Quantity.of(command.quantity()));
            repo.save(item);
            return true;
        });
    }

    private void withRetryVoid(BooleanSupplier op) {
        int attempts = 0;
        while (true) {
            try {
                if (op.getAsBoolean()) return;
            } catch (OptimisticLockingFailureException ex) {
                if (++attempts >= 3) throw ex;
                continue;
            }
            return;
        }
    }
}
