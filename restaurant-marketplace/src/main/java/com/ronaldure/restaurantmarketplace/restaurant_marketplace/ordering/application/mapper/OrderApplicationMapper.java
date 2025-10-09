// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/application/mapper/OrderApplicationMapper.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.Order;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderLine;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderStatus;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Quantity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class OrderApplicationMapper {

    /** Detalle completo para dueño/admin. */
    public OrderDetailView toDetailView(Order order) {
        Objects.requireNonNull(order, "order is required");

        Long id          = unwrapId(order);
        Long tenantId    = unwrap(order.tenantId());
        Long customerId  = order.customerId() != null ? order.customerId().value() : null;
        String status    = unwrap(order.status());
        BigDecimal total = unwrapAmount(order.total());
        String currency  = unwrapCurrency(order.total());
        Instant created  = order.createdAt();

        List<OrderDetailView.LineView> lines = order.lines().stream()
                .map(this::toLineView)
                .collect(Collectors.toList());

        return OrderDetailView.of(
                id, tenantId, customerId, status, total, currency, created, lines
        );
    }

    /** Tarjeta/resumen para listados admin. */
    public OrderCardView toCardView(Order order) {
        Objects.requireNonNull(order, "order is required");

        Long id          = unwrapId(order);
        String status    = unwrap(order.status());
        BigDecimal total = unwrapAmount(order.total());
        String currency  = unwrapCurrency(order.total());
        Instant created  = order.createdAt();
        int itemsCount   = order.lines().stream().mapToInt(l -> l.qty().value()).sum();

        return OrderCardView.of(
                id, status, total, currency, itemsCount, created
        );
    }

    // ---------- Helpers de mapeo de líneas ----------
    private OrderDetailView.LineView toLineView(OrderLine l) {
        Long productId    = l.productId();
        String name       = l.productName();
        BigDecimal unit   = unwrapAmount(l.unitPrice());
        String currency   = unwrapCurrency(l.unitPrice());
        int qty           = unwrap(l.qty());
        BigDecimal line   = unwrapAmount(l.lineTotal());

        return new OrderDetailView.LineView(productId, name, unit, currency, qty, line);
    }

    // ---------- Unwrapping de VOs (sin reflection) ----------
    private Long unwrapId(Order order) {
        return order.id() == null ? null : order.id().value();
    }

    private Long unwrap(TenantId tenantId) {
        return tenantId == null ? null : tenantId.value();
    }

    private String unwrap(OrderStatus st) {
        return st == null ? null : st.name();
    }

    private int unwrap(Quantity q) {
        return q == null ? 0 : q.value();
    }

    private BigDecimal unwrapAmount(Money m) {
        Objects.requireNonNull(m, "money is required");
        return m.amount();             // ← getter obligatorio
    }

    private String unwrapCurrency(Money m) {
        Objects.requireNonNull(m, "money is required");
        return m.currency();           // ← getter obligatorio
    }
}
