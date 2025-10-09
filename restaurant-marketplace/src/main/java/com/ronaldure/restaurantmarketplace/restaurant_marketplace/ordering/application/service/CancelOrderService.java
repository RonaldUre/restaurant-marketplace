package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.CancelOrderCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors.OrderCancellationNotAllowedException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors.OrderNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.mapper.OrderApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.CancelOrderUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.CustomerDirectoryPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.InventoryPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.NotificationPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.OrderRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.Order;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CancelOrderService implements CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final InventoryPort inventoryPort;
    private final NotificationPort notificationPort;
    private final CustomerDirectoryPort customerDirectoryPort;
    private final OrderApplicationMapper mapper;

    private final CurrentTenantProvider tenantProvider;
    private final AccessControl accessControl;
    private final TransactionTemplate tx;

    public CancelOrderService(OrderRepository orderRepository,
                              InventoryPort inventoryPort,
                              NotificationPort notificationPort,
                              CustomerDirectoryPort customerDirectoryPort,
                              OrderApplicationMapper mapper,
                              CurrentTenantProvider tenantProvider,
                              AccessControl accessControl,
                              PlatformTransactionManager txManager) {
        this.orderRepository = orderRepository;
        this.inventoryPort = inventoryPort;
        this.notificationPort = notificationPort;
        this.customerDirectoryPort = customerDirectoryPort;
        this.mapper = mapper;
        this.tenantProvider = tenantProvider;
        this.accessControl = accessControl;
        this.tx = new TransactionTemplate(txManager);
    }

    @Override
    public OrderDetailView cancel(CancelOrderCommand command) {
        // Admin del restaurante cancela en MVP
        accessControl.requireRole(Roles.RESTAURANT_ADMIN);
        TenantId tenantId = tenantProvider.requireCurrent();

        Order order = orderRepository
                .findById(OrderId.of(command.orderId()), tenantId)
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

        if (!order.status().name().equals("PENDING")) {
            throw new OrderCancellationNotAllowedException(order.id().value());
        }

        List<InventoryPort.Reservation> reservations = order.lines().stream()
                .map(l -> new InventoryPort.Reservation(l.productId(), l.qty().value()))
                .collect(Collectors.toList());

        tx.executeWithoutResult(status -> {
            if (!reservations.isEmpty()) {
                inventoryPort.release(tenantId, reservations);
            }
            order.cancel();
            orderRepository.save(order);
        });

        String email = customerDirectoryPort.getCustomerEmail(
                com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId.of(order.customerId().value())
        );
        String reason = command.reason() == null ? "cancelled_by_admin" : command.reason();
        notificationPort.sendOrderCancelled(email, order.id().value(), reason);

        return mapper.toDetailView(order);
    }
}
