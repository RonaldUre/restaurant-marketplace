// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/web/OrderPublicController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.PlaceOrderCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.GetOrderPublicQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.PlaceOrderUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.mapper.OrderWebMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request.PlaceOrderRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.response.OrderDetailResponse;
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
    private final GetOrderPublicQuery getOrderPublic;
    private final OrderWebMapper webMapper;

    public OrderPublicController(PlaceOrderUseCase placeOrder,
                                 GetOrderPublicQuery getOrderPublic,
                                 OrderWebMapper webMapper) {
        this.placeOrder = placeOrder;
        this.getOrderPublic = getOrderPublic;
        this.webMapper = webMapper;
    }

    // Place order → 201 Created + Location
    @PostMapping
    public ResponseEntity<OrderDetailResponse> place(
            @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestBody @Valid PlaceOrderRequest body) {

        PlaceOrderCommand cmd = webMapper.toCommand(body, idempotencyKey);
        OrderDetailView view = placeOrder.place(cmd);

        OrderDetailResponse resp = webMapper.toDetailResponse(view);
        return ResponseEntity
                .created(URI.create("/orders/" + resp.id()))
                .body(resp);
    }

    // Public get (owner-only) → 200 OK
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> get(@PathVariable("id") Long id) {
        OrderDetailView view = getOrderPublic.get(id);
        return ResponseEntity.ok(webMapper.toDetailResponse(view));
    }
}
