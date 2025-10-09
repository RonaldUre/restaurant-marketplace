package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.ConfirmPaymentCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors.OrderAlreadyPaidException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors.OrderNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.mapper.OrderApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.ConfirmPaymentUseCase;
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
public class ConfirmPaymentService implements ConfirmPaymentUseCase {

    private final OrderRepository orderRepository;
    private final InventoryPort inventoryPort;
    private final NotificationPort notificationPort;
    private final CustomerDirectoryPort customerDirectoryPort;
    private final OrderApplicationMapper mapper;

    private final CurrentTenantProvider tenantProvider;
    private final AccessControl accessControl;
    private final TransactionTemplate tx;

    public ConfirmPaymentService(OrderRepository orderRepository,
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
    public OrderDetailView confirm(ConfirmPaymentCommand command) {
        // Manual/administrativo: protegerlo
        accessControl.requireRole(Roles.RESTAURANT_ADMIN);
        TenantId tenantId = tenantProvider.requireCurrent();

        Order order = orderRepository
                .findById(OrderId.of(command.orderId()), tenantId)
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

        if (order.status().name().equals("PAID")) {
            throw new OrderAlreadyPaidException(order.id().value());
        }

        // Construir reservas desde las líneas del pedido (qty > 0)
        List<InventoryPort.Reservation> reservations = order.lines().stream()
                .map(l -> new InventoryPort.Reservation(l.productId(), l.qty().value()))
                .collect(Collectors.toList());

        tx.executeWithoutResult(status -> {
            if (!reservations.isEmpty()) {
                inventoryPort.confirm(tenantId, reservations);
            }
            order.markPaid();
            orderRepository.save(order);
        });

        String email = customerDirectoryPort.getCustomerEmail(
                com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId.of(order.customerId().value())
        );
        String restaurantName = "Restaurant #" + tenantId.value();
        String summary = order.lines().size() + " items · " + order.total();
        notificationPort.sendOrderConfirmed(email, order.id().value(), restaurantName, summary);

        return mapper.toDetailView(order);
    }
}
