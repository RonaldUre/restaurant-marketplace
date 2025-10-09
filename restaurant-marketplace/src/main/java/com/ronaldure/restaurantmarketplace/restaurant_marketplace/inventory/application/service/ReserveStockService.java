package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.command.ReserveStockCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.errors.InsufficientStockException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.errors.InventoryItemNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.in.ReserveStockUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out.InventoryRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.InventoryItem;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Quantity;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BooleanSupplier;

/**
 * Nota: no forzamos rol RESTAURANT_ADMIN porque será usado por Ordering.
 * Su seguridad se basa en el tenant en contexto + pertenencia del producto (validada al nivel de Ordering o adapter).
 */
@Service
public class ReserveStockService implements ReserveStockUseCase {

    private final InventoryRepository repo;
    private final CurrentTenantProvider tenantProvider;

    public ReserveStockService(InventoryRepository repo,
                               CurrentTenantProvider tenantProvider) {
        this.repo = repo;
        this.tenantProvider = tenantProvider;
    }

    @Override
    @Transactional
    public void reserve(ReserveStockCommand command) {
        TenantId tenantId = tenantProvider.requireCurrent();

        // Fast path: atomic SQL (si disponible)
        // boolean ok = repo.reserveAtomic(tenantId, command.productId(), command.quantity());
        // if (!ok) throw InsufficientStockException.forReserve(command.productId(), command.quantity());

        withRetryVoid(() -> {
            InventoryItem item = repo.findByTenantAndProduct(tenantId, command.productId())
                    .orElseThrow(() -> new InventoryItemNotFoundException(command.productId()));
            try {
                item.reserve(Quantity.of(command.quantity()));
            } catch (IllegalStateException ex) {
                throw InsufficientStockException.forReserve(command.productId(), command.quantity());
            }
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
