package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.PlaceOrderCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.factory.OrderFactory;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.mapper.OrderApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.PlaceOrderUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.Order;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentUserProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
public class PlaceOrderService implements PlaceOrderUseCase {

    private final CatalogPricingPort catalogPricingPort;
    private final InventoryPort inventoryPort;
    private final OrderRepository orderRepository;
    private final OrderFactory orderFactory;
    private final OrderApplicationMapper mapper;

    private final CurrentUserProvider userProvider;
    private final AccessControl accessControl;
    private final TransactionTemplate tx;

    public PlaceOrderService(CatalogPricingPort catalogPricingPort,
                             InventoryPort inventoryPort,
                             OrderRepository orderRepository,
                             OrderFactory orderFactory,
                             OrderApplicationMapper mapper,
                             CurrentUserProvider userProvider,
                             AccessControl accessControl,
                             PlatformTransactionManager txManager) {
        this.catalogPricingPort = catalogPricingPort;
        this.inventoryPort = inventoryPort;
        this.orderRepository = orderRepository;
        this.orderFactory = orderFactory;
        this.mapper = mapper;
        this.userProvider = userProvider;
        this.accessControl = accessControl;
        this.tx = new TransactionTemplate(txManager);
    }

     @Override
    public OrderDetailView place(PlaceOrderCommand command) {
        accessControl.requireRole(Roles.CUSTOMER);
        final TenantId tenantId = TenantId.of(command.restaurantId());
        final UserId customerId = userProvider.requireAuthenticated().userId();
        
        // La idempotencia para la creación es menos crítica ahora, pero se puede mantener si es necesario
        // para evitar crear múltiples pedidos PENDIENTES idénticos.

        // 1. Pricing
        final var items = command.items().stream()
                .map(i -> new CatalogPricingPort.Item(i.productId(), i.qty()))
                .toList();
        final var priced = catalogPricingPort.priceAndValidate(tenantId, items);

        // 2. Crear el agregado en PENDING
        final Order draft = orderFactory.newFrom(tenantId, customerId, priced);
        
        // 3. Calcular reservas de inventario
        final List<InventoryPort.Reservation> reservations =
                priced.requiresInventory() ? orderFactory.reservationsFrom(priced) : List.of();

        // 4. TX: Guardar pedido + Reservar stock
        Order savedOrder = tx.execute(status -> {
            Order s = orderRepository.save(draft);
            if (!reservations.isEmpty()) {
                inventoryPort.reserve(tenantId, reservations);
            }
            return s;
        });

        // 5. Devolver el pedido en estado PENDING. No se envía email de confirmación aún.
        return mapper.toDetailView(savedOrder);
    }
}