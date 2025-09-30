package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.PlaceOrderCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors.DuplicateIdempotencyKeyException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors.PaymentDeclinedException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.factory.OrderFactory;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.mapper.OrderApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.PlaceOrderUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.Order;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderId;
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
    private final PaymentsPort paymentsPort;
    private final NotificationPort notificationPort;
    private final CustomerDirectoryPort customerDirectoryPort;
    private final IdempotencyStore idempotencyStore;
    private final OrderRepository orderRepository;
    private final OrderFactory orderFactory;
    private final OrderApplicationMapper mapper;

    private final CurrentUserProvider userProvider;
    private final AccessControl accessControl;
    private final TransactionTemplate tx;

    public PlaceOrderService(CatalogPricingPort catalogPricingPort,
                             InventoryPort inventoryPort,
                             PaymentsPort paymentsPort,
                             NotificationPort notificationPort,
                             CustomerDirectoryPort customerDirectoryPort,
                             IdempotencyStore idempotencyStore,
                             OrderRepository orderRepository,
                             OrderFactory orderFactory,
                             OrderApplicationMapper mapper,
                             CurrentUserProvider userProvider,
                             AccessControl accessControl,
                             PlatformTransactionManager txManager) {
        this.catalogPricingPort = catalogPricingPort;
        this.inventoryPort = inventoryPort;
        this.paymentsPort = paymentsPort;
        this.notificationPort = notificationPort;
        this.customerDirectoryPort = customerDirectoryPort;
        this.idempotencyStore = idempotencyStore;
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
        final String idemKey = (command.idempotencyKey() != null && !command.idempotencyKey().isBlank())
                ? command.idempotencyKey()
                : null;

        // Early return si ya existe (fast-path)
        if (idemKey != null) {
            var existing = idempotencyStore.find(tenantId, customerId, idemKey);
            if (existing.isPresent()) {
                var prev = orderRepository
                        .findById(OrderId.of(existing.get().orderId()), tenantId)
                        .orElseThrow(() -> new IllegalStateException("Inconsistent idempotency store: order missing"));
                return mapper.toDetailView(prev);
            }
        }

        // Pricing
        final var items = command.items().stream()
                .map(i -> new CatalogPricingPort.Item(i.productId(), i.qty()))
                .toList();
        final var priced = catalogPricingPort.priceAndValidate(tenantId, items);

        // Agregado en CREATED
        final Order draft = orderFactory.newFrom(tenantId, customerId, priced);

        // Reservas calculadas una sola vez
        final List<InventoryPort.Reservation> reservations =
                priced.requiresInventory() ? orderFactory.reservationsFrom(priced) : List.of();

        // ---------- TX A: guardar pedido + registrar idempotencia + reservar stock ----------
        final Order saved;
        try {
            saved = tx.execute(status -> {
                Order s = orderRepository.save(draft);
                if (idemKey != null) {
                    // si colisiona UNIQUE, esta Tx revierte (no queda pedido/ni reservas)
                    idempotencyStore.save(tenantId, customerId, idemKey, s.id().value());
                }
                if (!reservations.isEmpty()) {
                    inventoryPort.reserve(tenantId, reservations);
                }
                return s;
            });
        } catch (DuplicateIdempotencyKeyException collide) {
            // GOLD: devolver el pedido previamente creado (idempotencia real)
            var existing = idempotencyStore.find(tenantId, customerId, idemKey)
                    .orElseThrow(() -> new IllegalStateException("Idempotency key exists but not found after collision"));
            var prev = orderRepository
                    .findById(OrderId.of(existing.orderId()), tenantId)
                    .orElseThrow(() -> new IllegalStateException("Inconsistent idempotency store: order missing"));
            return mapper.toDetailView(prev);
        }

        // ---------- Cobro (fuera de TX) ----------
        PaymentsPort.ChargeResult charge =
                paymentsPort.charge(orderFactory.toChargeRequest(saved, command.paymentMethod()));

        if (charge.approved()) {
            // TX B: confirmar stock + marcar pagado
            tx.executeWithoutResult(status -> {
                if (!reservations.isEmpty()) inventoryPort.confirm(tenantId, reservations);
                saved.markPaid();
                orderRepository.save(saved);
            });

            // Notificar
            String email = customerDirectoryPort.getCustomerEmail(customerId);
            String summary = orderFactory.buildSummary(saved);
            String restaurantName = "Restaurant #" + tenantId.value();
            notificationPort.sendOrderConfirmed(email, saved.id().value(), restaurantName, summary);

            return mapper.toDetailView(saved);
        } else {
            // TX B (rechazo): liberar stock + cancelar
            tx.executeWithoutResult(status -> {
                if (!reservations.isEmpty()) inventoryPort.release(tenantId, reservations);
                saved.cancel();
                orderRepository.save(saved);
            });

            String email = customerDirectoryPort.getCustomerEmail(customerId);
            notificationPort.sendPaymentFailed(email, saved.id().value(), charge.reason());

            throw new PaymentDeclinedException(saved.id().value(), charge.reason());
        }
    }
}