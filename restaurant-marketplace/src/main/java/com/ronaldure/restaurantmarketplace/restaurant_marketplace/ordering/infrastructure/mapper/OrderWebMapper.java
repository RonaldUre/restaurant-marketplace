// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/mapper/OrderWebMapper.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.CancelOrderCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.ConfirmPaymentCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.PlaceOrderCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.query.ListOrdersAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request.CancelOrderRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request.ConfirmPaymentRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request.ListOrdersAdminRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request.PlaceOrderRequest;
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

    // ---------- Web -> App (commands) ----------

    /**
     * If the Idempotency-Key is also received via header, prefer it over body.
     */
    public PlaceOrderCommand toCommand(PlaceOrderRequest req, String idempotencyKeyHeader) {
        String key = firstNonBlank(idempotencyKeyHeader, req.idempotencyKey());
        List<PlaceOrderCommand.Item> items = req.items().stream()
                .map(i -> new PlaceOrderCommand.Item(i.productId(), i.qty()))
                .toList();

        // FIX: respect PlaceOrderCommand signature: (restaurantId, items, paymentMethod, idempotencyKey)
        return new PlaceOrderCommand(
                req.restaurantId(),
                items,
                req.paymentMethod(),
                nullIfBlank(key)
        );
    }

    /** Convenience overload when no header is handled. */
    public PlaceOrderCommand toCommand(PlaceOrderRequest req) {
        return toCommand(req, null);
    }

    public ConfirmPaymentCommand toCommand(ConfirmPaymentRequest req) {
        // FIX: ConfirmPaymentCommand requires (orderId, transactionId). For MVP we pass null.
        return new ConfirmPaymentCommand(req.orderId(), null);
    }

    public CancelOrderCommand toCommand(CancelOrderRequest req) {
        return new CancelOrderCommand(req.orderId(), nullIfBlank(req.reason()));
    }

    // ---------- Web -> App (query params) ----------

    public ListOrdersAdminQueryParams toParams(ListOrdersAdminRequest req) {
        return req.toQueryParams();
    }

    // ---------- helpers ----------

    private static String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private static String firstNonBlank(String a, String b) {
        return (a != null && !a.isBlank()) ? a : b;
    }
}
