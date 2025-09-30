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

        // TODO: si tus views no tienen factory estática, reemplaza por constructor o builder correspondiente
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

        // TODO: ajusta a la firma real de tu OrderCardView
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

        // TODO: ajusta a la firma real de tu LineView
        return new OrderDetailView.LineView(productId, name, unit, currency, qty, line);
    }

    // ---------- Unwrapping de VOs ----------

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
        // TODO: cambia por m.amount() si tu VO Money lo expone así
        try {
            return (BigDecimal) Money.class.getMethod("amount").invoke(m);
        } catch (Exception ignore) {
            // Fallback: parsear toString() si aún no tienes getters en Money
            // Recomendado: añade getters en Money (amount(), currency()).
            String s = String.valueOf(m);
            // Muy básico: extrae dígitos; reemplaza por tu propia lógica si es necesario
            return new BigDecimal(s.replaceAll("[^0-9.,-]", "").replace(",", "."));
        }
    }

    private String unwrapCurrency(Money m) {
        // TODO: cambia por m.currency() si tu VO Money lo expone así
        try {
            return (String) Money.class.getMethod("currency").invoke(m);
        } catch (Exception ignore) {
            return "USD"; // fallback seguro; idealmente obtén del VO
        }
    }
}
