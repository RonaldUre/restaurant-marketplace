package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.domain;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;

import java.time.Instant;
import java.util.Objects;

public final class PaymentTransaction {

    public enum Status { INITIATED, APPROVED, DECLINED }

    private final OrderId orderId;
    private final TenantId tenantId;
    private final Money amount;
    private final String method; // e.g. "FAKE","CARD"
    private final Status status;
    private final String txId;   // puede ser null (fake)
    private final String reason; // motivo rechazo u observación
    private final Instant createdAt;

    private PaymentTransaction(OrderId orderId, TenantId tenantId, Money amount,
                               String method, Status status, String txId, String reason, Instant createdAt) {
        this.orderId = Objects.requireNonNull(orderId);
        this.tenantId = Objects.requireNonNull(tenantId);
        this.amount = Objects.requireNonNull(amount);
        this.method = Objects.requireNonNull(method);
        this.status = Objects.requireNonNull(status);
        this.txId = txId;
        this.reason = reason;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static PaymentTransaction initiated(OrderId orderId, TenantId tenantId, Money amount, String method){
        return new PaymentTransaction(orderId, tenantId, amount, method, Status.INITIATED, null, null, Instant.now());
    }

    public static PaymentTransaction approved(OrderId orderId, TenantId tenantId, Money amount, String method, String txId){
        return new PaymentTransaction(orderId, tenantId, amount, method, Status.APPROVED, txId, null, Instant.now());
    }

    public static PaymentTransaction declined(OrderId orderId, TenantId tenantId, Money amount, String method, String reason){
        return new PaymentTransaction(orderId, tenantId, amount, method, Status.DECLINED, null, reason, Instant.now());
    }

    public OrderId orderId(){ return orderId; }
    public TenantId tenantId(){ return tenantId; }
    public Money amount(){ return amount; }
    public String method(){ return method; }
    public Status status(){ return status; }
    public String txId(){ return txId; }
    public String reason(){ return reason; }
    public Instant createdAt(){ return createdAt; }
}
