package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root: Order
 * Reglas:
 * - 1 pedido = 1 restaurant (tenant).
 * - Líneas son snapshot (nombre, precio, qty, total).
 * - Estados: CREATED -> PAID | CANCELLED.
 */
public final class Order {

    private OrderId id; // asignado por persistencia
    private final TenantId tenantId;
    private final CustomerId customerId;

    private OrderStatus status;
    private final List<OrderLine> lines;
    private Money total;                 // snapshot del total
    private final Instant createdAt;
    private Instant updatedAt;

    private Order(OrderId id,
                  TenantId tenantId,
                  CustomerId customerId,
                  List<OrderLine> lines,
                  Money total,
                  OrderStatus status,
                  Instant createdAt,
                  Instant updatedAt) {

        this.id = id; // puede ser null en create()
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.customerId = Objects.requireNonNull(customerId, "customerId");
        this.lines = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(lines, "lines")));
        this.total = Objects.requireNonNull(total, "total");
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;

        validateInvariants();
    }

    /** Creación de pedido en estado CREATED. El cálculo de líneas/total lo hace la capa de aplicación (pricing). */
    public static Order create(TenantId tenantId,
                               CustomerId customerId,
                               List<OrderLine> lines,
                               Money total) {
        return new Order(null, tenantId, customerId, lines, total, OrderStatus.PENDING, Instant.now(), Instant.now());
    }

    /** Rehidratación desde persistencia. */
    public static Order rehydrate(OrderId id,
                                  TenantId tenantId,
                                  CustomerId customerId,
                                  List<OrderLine> lines,
                                  Money total,
                                  OrderStatus status,
                                  Instant createdAt,
                                  Instant updatedAt) {
        return new Order(Objects.requireNonNull(id, "id"), tenantId, customerId, lines, total, status, createdAt, updatedAt);
    }

    /** Asigna ID técnico una sola vez. */
    public void assignId(OrderId id){
        if (this.id != null) throw new IllegalStateException("Order id already assigned");
        this.id = Objects.requireNonNull(id, "id");
    }

    /** Transición CREATED -> PAID. */
    public void markPaid(){
        if (status == OrderStatus.PAID) throw new IllegalStateException("Order already PAID");
        if (status == OrderStatus.CANCELLED) throw new IllegalStateException("Cancelled order cannot be PAID");
        this.status = OrderStatus.PAID;
        touch();
    }

    /** Cancelación permitida si aún está CREATED (política MVP). */
    public void cancel(){
        if (status == OrderStatus.PAID) throw new IllegalStateException("Paid order cannot be cancelled");
        this.status = OrderStatus.CANCELLED;
        touch();
    }

    private void validateInvariants(){
        if (lines.isEmpty()) throw new IllegalStateException("Order must have at least one line");
        // Validaciones ligeras: totales, moneda consistente, etc.
        // No asumimos operaciones aritméticas de Money aquí; la capa de aplicación se encarga del cálculo.
    }

    private void touch(){ this.updatedAt = Instant.now(); }

    // Getters
    public OrderId id(){ return id; }
    public TenantId tenantId(){ return tenantId; }
    public CustomerId customerId(){ return customerId; }
    public OrderStatus status(){ return status; }
    public List<OrderLine> lines(){ return lines; }
    public Money total(){ return total; }
    public Instant createdAt(){ return createdAt; }
    public Instant updatedAt(){ return updatedAt; }
}
