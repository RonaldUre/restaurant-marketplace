// infrastructure/mapper/OrderWebMapper.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.CancelOrderCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.CaptureOrderPaymentCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.CreateOrderPaymentCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.PlaceOrderCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.query.ListOrdersAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.query.ListOrdersPublicQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.ApprovalLinkView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.response.CreatePaymentResponseDto;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.response.OrderCardResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.response.OrderDetailResponse;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class OrderWebMapper {

    // ---------- App -> Web (responses) ----------

    public OrderDetailResponse toDetailResponse(OrderDetailView view) {
        return OrderDetailResponse.from(view);
    }

    public OrderCardResponse toCardResponse(OrderCardView view) {
        return OrderCardResponse.from(view);
    }

    public CreatePaymentResponseDto toPaymentApprovalResponse(ApprovalLinkView view) {
        return new CreatePaymentResponseDto(view.approvalUrl());
    }

    // ---------- Web -> App (commands) ----------

    public PlaceOrderCommand toPlaceOrderCommand(PlaceOrderRequest req) {
        List<PlaceOrderCommand.Item> items = req.items().stream()
                .map(i -> new PlaceOrderCommand.Item(i.productId(), i.qty()))
                .toList();
        return new PlaceOrderCommand(req.restaurantId(), items);
    }

    public CreateOrderPaymentCommand toCreateOrderPaymentCommand(Long orderId, CreatePaymentRequestDto dto) {
        return new CreateOrderPaymentCommand(orderId, dto.paymentMethod());
    }

    public CaptureOrderPaymentCommand toCaptureOrderPaymentCommand(Long orderId, CapturePaymentRequestDto dto,
            String idempotencyKey) {
        return new CaptureOrderPaymentCommand(orderId, dto.paymentProviderOrderId(), idempotencyKey);
    }

    public CancelOrderCommand toCancelOrderCommand(Long orderId, CancelOrderRequest req) {
        String reason = (req != null) ? req.reason() : null;
        return new CancelOrderCommand(orderId, nullIfBlank(reason));
    }

    // ---------- Web -> App (query params) ----------

    public ListOrdersAdminQueryParams toParams(ListOrdersAdminRequest req) {
        return req.toQueryParams();
    }

    public ListOrdersPublicQueryParams toParams(ListOrdersPublicRequest req) {
        return req.toQueryParams();
    }

    // ---------- helpers ----------

    private static String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
