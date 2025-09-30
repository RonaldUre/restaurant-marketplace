package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.adapter;

import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.errors.InsufficientStockException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.errors.InventoryItemNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out.InventoryRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.InventoryItem;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.InventoryPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Quantity;

@Component
public class InventoryPortApplicationAdapter implements InventoryPort {

    private final InventoryRepository repo;

    public InventoryPortApplicationAdapter(InventoryRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional
    public void reserve(TenantId tenantId, List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) return;

        for (Reservation r : reservations) {
            final long productId = r.productId();
            final int qty = r.qty();
            if (qty <= 0) throw new IllegalArgumentException("qty must be > 0 for productId=" + productId);

            if (repo.reserveAtomic(tenantId, productId, qty)) continue;

            withRetry(() -> {
                InventoryItem item = repo.findByTenantAndProduct(tenantId, productId)
                        .orElseThrow(() -> new InventoryItemNotFoundException(productId));
                try {
                    item.reserve(Quantity.of(qty)); // ilimitado => no-op
                } catch (IllegalStateException ex) {
                    throw InsufficientStockException.forReserve(productId, qty);
                }
                repo.save(item);
            });
        }
    }

    @Override
    @Transactional
    public void release(TenantId tenantId, List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) return;

        for (Reservation r : reservations) {
            final long productId = r.productId();
            final int qty = r.qty();
            if (qty <= 0) throw new IllegalArgumentException("qty must be > 0 for productId=" + productId);

            if (repo.releaseAtomic(tenantId, productId, qty)) continue;

            withRetry(() -> {
                InventoryItem item = repo.findByTenantAndProduct(tenantId, productId)
                        .orElseThrow(() -> new InventoryItemNotFoundException(productId));
                item.release(Quantity.of(qty)); // ilimitado => no-op; limitado valida reserved>=qty
                repo.save(item);
            });
        }
    }

    @Override
    @Transactional
    public void confirm(TenantId tenantId, List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) return;

        for (Reservation r : reservations) {
            final long productId = r.productId();
            final int qty = r.qty();
            if (qty <= 0) throw new IllegalArgumentException("qty must be > 0 for productId=" + productId);

            if (repo.confirmAtomic(tenantId, productId, qty)) continue;

            withRetry(() -> {
                InventoryItem item = repo.findByTenantAndProduct(tenantId, productId)
                        .orElseThrow(() -> new InventoryItemNotFoundException(productId));
                try {
                    item.confirm(Quantity.of(qty)); // ilimitado => no-op; limitado resta reserved/available
                } catch (IllegalStateException ex) {
                    throw InsufficientStockException.forConfirm(productId, qty);
                }
                repo.save(item);
            });
        }
    }

    // ---- helpers ----
    private void withRetry(Runnable op) {
        int attempts = 0;
        while (true) {
            try {
                op.run();
                return;
            } catch (OptimisticLockingFailureException ex) {
                if (++attempts >= 3) throw ex;
            }
        }
    }
}
