// infrastructure/web/OrderPublicController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.CancelOrderCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.CaptureOrderPaymentCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.CreateOrderPaymentCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.PlaceOrderCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.CancelOwnOrderUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.CaptureOrderPaymentUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.CreateOrderPaymentUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.GetOrderPublicQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.ListOrdersPublicQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.PlaceOrderUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.ApprovalLinkView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.mapper.OrderWebMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request.CancelOrderRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request.CapturePaymentRequestDto;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request.CreatePaymentRequestDto;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request.ListOrdersPublicRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request.PlaceOrderRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.response.CreatePaymentResponseDto;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.response.OrderCardResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.response.OrderDetailResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@Validated
@RestController
@RequestMapping("/orders")
public class OrderPublicController {

    private final PlaceOrderUseCase placeOrder;
    private final CreateOrderPaymentUseCase createOrderPayment;
    private final CaptureOrderPaymentUseCase captureOrderPayment;
    private final ListOrdersPublicQuery listOrdersPublic;
    private final CancelOwnOrderUseCase cancelOwnOrder;
    private final GetOrderPublicQuery getOrderPublic;
    private final OrderWebMapper webMapper;

    public OrderPublicController(PlaceOrderUseCase placeOrder,
            CreateOrderPaymentUseCase createOrderPayment,
            CaptureOrderPaymentUseCase captureOrderPayment,
            GetOrderPublicQuery getOrderPublic,
            ListOrdersPublicQuery listOrdersPublic,
            CancelOwnOrderUseCase cancelOwnOrder,
            OrderWebMapper webMapper) {
        this.placeOrder = placeOrder;
        this.createOrderPayment = createOrderPayment;
        this.captureOrderPayment = captureOrderPayment;
        this.getOrderPublic = getOrderPublic;
        this.webMapper = webMapper;
        this.listOrdersPublic = listOrdersPublic;
        this.cancelOwnOrder = cancelOwnOrder;
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderCardResponse>> list(@Valid @ModelAttribute ListOrdersPublicRequest query) {
        var params = webMapper.toParams(query);
        PageResponse<OrderCardView> result = listOrdersPublic.list(params);

        var items = result.items().stream()
                .map(webMapper::toCardResponse)
                .toList();

        return ResponseEntity.ok(new PageResponse<>(items, result.totalElements(), result.totalPages()));
    }

    // Place order → 201 Created + Location
    @PostMapping
    public ResponseEntity<OrderDetailResponse> place(@RequestBody @Valid PlaceOrderRequest body) {
        PlaceOrderCommand cmd = webMapper.toPlaceOrderCommand(body);
        OrderDetailView view = placeOrder.place(cmd);
        OrderDetailResponse resp = webMapper.toDetailResponse(view);

        return ResponseEntity
                .created(URI.create("/orders/" + resp.id()))
                .body(resp);
    }

    // PASO 1: Inicia el pago y devuelve el enlace de aprobación
    @PostMapping("/{id}/payment/create")
    public ResponseEntity<CreatePaymentResponseDto> createPayment(
            @PathVariable("id") Long orderId,
            @RequestBody @Valid CreatePaymentRequestDto body) {

        CreateOrderPaymentCommand cmd = webMapper.toCreateOrderPaymentCommand(orderId, body);
        ApprovalLinkView view = createOrderPayment.create(cmd);

        return ResponseEntity.ok(webMapper.toPaymentApprovalResponse(view));
    }

    // PASO 2: Captura el pago después de la aprobación del cliente
    @PostMapping("/{id}/payment/capture")
    public ResponseEntity<OrderDetailResponse> capturePayment(
            @PathVariable("id") Long orderId,
            @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestBody @Valid CapturePaymentRequestDto body) {

        CaptureOrderPaymentCommand cmd = webMapper.toCaptureOrderPaymentCommand(orderId, body, idempotencyKey);
        OrderDetailView view = captureOrderPayment.capture(cmd);

        return ResponseEntity.ok(webMapper.toDetailResponse(view));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderDetailResponse> cancelOwn(@PathVariable("id") Long id,
            @RequestBody(required = false) @Valid CancelOrderRequest body) {
        String reason = (body != null) ? body.reason() : null;
        var view = cancelOwnOrder.cancel(new CancelOrderCommand(id, reason));
        return ResponseEntity.ok(webMapper.toDetailResponse(view));
    }

    // Public get (owner-only) → 200 OK
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> get(@PathVariable("id") Long id) {
        OrderDetailView view = getOrderPublic.get(id);
        return ResponseEntity.ok(webMapper.toDetailResponse(view));
    }
}
