package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.events;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out.InventoryRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.events.ProductCreatedEvent;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InventoryOnProductCreated {

    private final InventoryRepository inventoryRepository;

    public InventoryOnProductCreated(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /** Sincrónico, MISMA transacción que CreateProduct. Si falla, hace rollback del producto. */
    @EventListener
    @Transactional // se une a la TX del servicio si existe
    public void on(ProductCreatedEvent event) {
        inventoryRepository.createUnlimitedIfAbsent(event.tenantId(), event.productId());
    }
}
