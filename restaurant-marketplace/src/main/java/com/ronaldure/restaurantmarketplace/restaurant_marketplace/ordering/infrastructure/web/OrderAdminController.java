// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/web/OrderAdminController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.CancelOrderCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.ConfirmPaymentCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.query.ListOrdersAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.mapper.OrderWebMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request.CancelOrderRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request.ListOrdersAdminRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.response.OrderCardResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.response.OrderDetailResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/orders")
public class OrderAdminController {

    private final GetOrderAdminQuery getOrderAdmin;
    private final ListOrdersAdminQuery listOrdersAdmin;
    private final CancelOrderUseCase cancelOrder;
    private final ConfirmPaymentUseCase confirmPayment;
    private final OrderWebMapper webMapper;

    public OrderAdminController(GetOrderAdminQuery getOrderAdmin,
                                ListOrdersAdminQuery listOrdersAdmin,
                                CancelOrderUseCase cancelOrder,
                                ConfirmPaymentUseCase confirmPayment,
                                OrderWebMapper webMapper) {
        this.getOrderAdmin = getOrderAdmin;
        this.listOrdersAdmin = listOrdersAdmin;
        this.cancelOrder = cancelOrder;
        this.confirmPayment = confirmPayment;
        this.webMapper = webMapper;
    }

    // Admin detail → 200 OK
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> get(@PathVariable("id") Long id) {
        OrderDetailView view = getOrderAdmin.get(id);
        return ResponseEntity.ok(webMapper.toDetailResponse(view));
    }

    // Admin list → 200 OK
    @GetMapping
    public ResponseEntity<PageResponse<OrderCardResponse>> list(@Valid @ModelAttribute ListOrdersAdminRequest query) {
        ListOrdersAdminQueryParams params = webMapper.toParams(query);
        PageResponse<OrderCardView> result = listOrdersAdmin.list(params);

        List<OrderCardResponse> items = result.items().stream()
                .map(webMapper::toCardResponse)
                .toList();

        return ResponseEntity.ok(new PageResponse<>(items, result.totalElements(), result.totalPages()));
    }

    // Cancel (CREATED only) → 200 OK
    // Path takes precedence for orderId; body.reason is optional
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderDetailResponse> cancel(@PathVariable("id") Long id,
                                                      @RequestBody(required = false) @Valid CancelOrderRequest body) {
        String reason = (body != null) ? body.reason() : null;
        OrderDetailView view = cancelOrder.cancel(new CancelOrderCommand(id, reason));
        return ResponseEntity.ok(webMapper.toDetailResponse(view));
    }

    // Manual confirm payment (admin) → 200 OK
    @PostMapping("/{id}/confirm-payment")
    public ResponseEntity<OrderDetailResponse> confirmPayment(@PathVariable("id") Long id) {
        OrderDetailView view = confirmPayment.confirm(new ConfirmPaymentCommand(id, null));
        return ResponseEntity.ok(webMapper.toDetailResponse(view));
    }
}
