package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment_transactions", indexes = {
        @Index(name = "idx_payment_tx_tenant", columnList = "tenant_id"),
        @Index(name = "idx_payment_tx_status", columnList = "status"),
        @Index(name = "idx_payment_tx_created", columnList = "created_at")
})
public class JpaPaymentTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Único por pedido (refuerza que haya solo 1 transacción por order en el MVP).
     */
    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "method", nullable = false, length = 50) // antes 32
    private String method;

    @Column(name = "status", nullable = false, length = 20) // antes 16
    private String status; // INITIATED | APPROVED | DECLINED

    @Column(name = "tx_id", length = 100) // antes 128
    private String txId;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public JpaPaymentTransactionEntity() {
    }

    // -------- Getters --------
    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getMethod() {
        return method;
    }

    public String getStatus() {
        return status;
    }

    public String getTxId() {
        return txId;
    }

    public String getReason() {
        return reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    // -------- Setters --------
    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /** Por si te olvidas de setear createdAt desde el servicio. */
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
