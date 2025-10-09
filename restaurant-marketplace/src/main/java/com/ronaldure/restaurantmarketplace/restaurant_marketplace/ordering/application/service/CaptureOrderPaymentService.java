// ordering/application/service/CaptureOrderPaymentService.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.CaptureOrderPaymentCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors.OrderNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors.PaymentDeclinedException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.factory.OrderFactory;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.mapper.OrderApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.CaptureOrderPaymentUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.*;
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
public class CaptureOrderPaymentService implements CaptureOrderPaymentUseCase {

    private final AccessControl accessControl;
    private final CurrentUserProvider userProvider;
    private final OrderRepository orderRepository;
    private final InventoryPort inventoryPort;
    private final PaymentsPort paymentsPort;
    private final NotificationPort notificationPort;
    private final CustomerDirectoryPort customerDirectoryPort;
    private final IdempotencyStore idempotencyStore;
    private final OrderFactory orderFactory;
    private final OrderApplicationMapper mapper;
    private final TransactionTemplate tx;

    public CaptureOrderPaymentService(AccessControl accessControl, CurrentUserProvider userProvider,
            OrderRepository orderRepository, InventoryPort inventoryPort, PaymentsPort paymentsPort,
            OrderFactory orderFactory, NotificationPort notificationPort, CustomerDirectoryPort customerDirectoryPort,
            IdempotencyStore idempotencyStore, OrderApplicationMapper mapper, PlatformTransactionManager txManager) {
        this.accessControl = accessControl;
        this.userProvider = userProvider;
        this.orderRepository = orderRepository;
        this.inventoryPort = inventoryPort;
        this.paymentsPort = paymentsPort;
        this.notificationPort = notificationPort;
        this.customerDirectoryPort = customerDirectoryPort;
        this.orderFactory = orderFactory;
        this.idempotencyStore = idempotencyStore;
        this.mapper = mapper;
        this.tx = new TransactionTemplate(txManager);
    }

    @Override
    public OrderDetailView capture(CaptureOrderPaymentCommand command) {
        accessControl.requireRole(Roles.CUSTOMER);
        final UserId customerId = userProvider.requireAuthenticated().userId();
        final OrderId orderId = OrderId.of(command.orderId());

        Order order = orderRepository.findById(orderId)
                .filter(o -> o.customerId().value() == Long.parseLong(customerId.value()))
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

        if (order.status() == OrderStatus.PAID) {
            return mapper.toDetailView(order);
        }

        if (order.status() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order is not in PENDING state. Current state: " + order.status());
        }

        final String idemKey = command.idempotencyKey();
        if (idemKey != null && !idemKey.isBlank()) {
            var existing = idempotencyStore.find(order.tenantId(), customerId, idemKey);
            if (existing.isPresent()) {
                Order previousOrder = orderRepository.findById(OrderId.of(existing.get().orderId()))
                        .orElseThrow(() -> new IllegalStateException("Inconsistent idempotency store: order missing"));
                return mapper.toDetailView(previousOrder);
            }
        }

        var request = new PaymentsPort.CapturePaymentRequest(
                command.paymentProviderOrderId(),
                order.id().value(),
                order.tenantId(),
                order.total());
        PaymentsPort.CapturePaymentResult charge = paymentsPort.capturePayment(request);

        final List<InventoryPort.Reservation> reservations = orderFactory.reservationsFromDomain(order);

        if (charge.approved()) {
            tx.executeWithoutResult(status -> {
                if (!reservations.isEmpty()) {
                    inventoryPort.confirm(order.tenantId(), reservations);
                }
                order.markPaid();
                orderRepository.save(order);
                if (idemKey != null && !idemKey.isBlank()) {
                    idempotencyStore.save(order.tenantId(), customerId, idemKey, order.id().value());
                }
            });

            String email = customerDirectoryPort.getCustomerEmail(customerId);
            if (email != null) {
                String summary = orderFactory.buildSummary(order);
                String restaurantName = "Restaurante #" + order.tenantId().value();
                notificationPort.sendOrderConfirmed(email, order.id().value(), restaurantName, summary);
            }
        } else {
            tx.executeWithoutResult(status -> {
                if (!reservations.isEmpty())
                    inventoryPort.release(order.tenantId(), reservations);
                order.cancel();
                orderRepository.save(order);
            });

            String email = customerDirectoryPort.getCustomerEmail(customerId);
            if (email != null) {
                notificationPort.sendPaymentFailed(email, order.id().value(), charge.reason());
            }
            throw new PaymentDeclinedException(order.id().value(), charge.reason());
        }

        return mapper.toDetailView(order);
    }
}
