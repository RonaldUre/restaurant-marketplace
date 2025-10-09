// ordering/application/service/CreateOrderPaymentService.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.CreateOrderPaymentCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors.OrderNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.factory.OrderFactory;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.CreateOrderPaymentUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.OrderRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.PaymentsPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.ApprovalLinkView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.Order;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderStatus;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentUserProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateOrderPaymentService implements CreateOrderPaymentUseCase {

    private final AccessControl accessControl;
    private final CurrentUserProvider userProvider;
    private final OrderRepository orderRepository;
    private final PaymentsPort paymentsPort;
    private final OrderFactory orderFactory;


    public CreateOrderPaymentService(AccessControl accessControl, CurrentUserProvider userProvider,
            OrderRepository orderRepository, PaymentsPort paymentsPort, OrderFactory orderFactory) {
        this.accessControl = accessControl;
        this.userProvider = userProvider;
        this.orderRepository = orderRepository;
        this.paymentsPort = paymentsPort;
        this.orderFactory = orderFactory;

    }

    @Override
    @Transactional
    public ApprovalLinkView create(CreateOrderPaymentCommand command) {
        accessControl.requireRole(Roles.CUSTOMER);
        final UserId customerId = userProvider.requireAuthenticated().userId();
        final OrderId orderId = OrderId.of(command.orderId());

        // Cargar el pedido y validar que pertenece al cliente y está pendiente
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.customerId().value() == Long.parseLong(customerId.value()))
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

        if (order.status() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order is not in PENDING state. Current state: " + order.status());
        }

        // Usamos la fábrica para crear la petición de pago. ¡Esta es la forma correcta!
        var request = orderFactory.toCreatePaymentRequest(order, command.paymentMethod());

        // Llamar al puerto de pagos para obtener el enlace de aprobación
        PaymentsPort.CreatePaymentResult result = paymentsPort.createPayment(request);

        return new ApprovalLinkView(result.approvalUrl());
    }
}