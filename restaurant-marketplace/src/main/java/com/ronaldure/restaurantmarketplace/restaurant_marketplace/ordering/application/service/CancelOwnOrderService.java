package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.CancelOrderCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors.OrderCancellationNotAllowedException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors.OrderNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.factory.OrderFactory;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.mapper.OrderApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.CancelOwnOrderUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.CustomerDirectoryPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.InventoryPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.NotificationPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.OrderRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.Order;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderStatus;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentUserProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
public class CancelOwnOrderService implements CancelOwnOrderUseCase {

    private final AccessControl accessControl;
    private final CurrentUserProvider userProvider;
    private final OrderRepository orderRepository;
    private final InventoryPort inventoryPort;
    private final NotificationPort notificationPort;
    private final CustomerDirectoryPort customerDirectoryPort;
    private final OrderFactory orderFactory;
    private final OrderApplicationMapper mapper;
    private final TransactionTemplate tx;

    public CancelOwnOrderService(AccessControl accessControl, CurrentUserProvider userProvider,
                                 OrderRepository orderRepository, InventoryPort inventoryPort,
                                 NotificationPort notificationPort, CustomerDirectoryPort customerDirectoryPort,
                                 OrderFactory orderFactory, OrderApplicationMapper mapper,
                                 PlatformTransactionManager txManager) {
        this.accessControl = accessControl;
        this.userProvider = userProvider;
        this.orderRepository = orderRepository;
        this.inventoryPort = inventoryPort;
        this.notificationPort = notificationPort;
        this.customerDirectoryPort = customerDirectoryPort;
        this.orderFactory = orderFactory;
        this.mapper = mapper;
        this.tx = new TransactionTemplate(txManager);
    }

    @Override
    public OrderDetailView cancel(CancelOrderCommand command) {
        accessControl.requireRole(Roles.CUSTOMER);
        final UserId owner = userProvider.requireAuthenticated().userId();
        final OrderId orderId = OrderId.of(command.orderId());

        Order order = orderRepository.findById(orderId)
                .filter(o -> o.customerId().value() == Long.parseLong(owner.value()))
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

        if (order.status() != OrderStatus.PENDING) {
            throw new OrderCancellationNotAllowedException(order.id().value());
        }

        final List<InventoryPort.Reservation> reservations = orderFactory.reservationsFromDomain(order);

        tx.executeWithoutResult(s -> {
            if (!reservations.isEmpty()) {
                inventoryPort.release(order.tenantId(), reservations);
            }
            order.cancel();
            orderRepository.save(order);
        });

        String email = customerDirectoryPort.getCustomerEmail(owner);
        if (email != null) {
            String reason = (command.reason() == null || command.reason().isBlank())
                    ? "cancelled_by_owner"
                    : command.reason();
            notificationPort.sendOrderCancelled(email, order.id().value(), reason);
        }

        return mapper.toDetailView(order);
    }
}
