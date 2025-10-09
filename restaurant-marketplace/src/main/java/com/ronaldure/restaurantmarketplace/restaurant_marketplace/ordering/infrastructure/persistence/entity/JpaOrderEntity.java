// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/persistence/entity/JpaOrderEntity.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.entity;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(
    name = "orders",
    indexes = {
        @Index(name = "idx_orders_tenant_status_created", columnList = "tenant_id,status,created_at"),
        @Index(name = "idx_orders_customer_created", columnList = "customer_id,created_at")
    }
)
@DynamicUpdate
public class JpaOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Multitenancy: strong isolation at query-level; FK enforced by DB
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Aggregate relationship: Order -> OrderLines
    @OneToMany(
        mappedBy = "order",
        cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE },
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<JpaOrderLineEntity> lines = new ArrayList<>();

    // --------- lifecycle & helpers ---------

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public void addLine(JpaOrderLineEntity line) {
        // Maintain bidirectional consistency
        line.setOrder(this);
        lines.add(line);
    }

    public void removeLine(JpaOrderLineEntity line) {
        lines.remove(line);
        line.setOrder(null);
    }

    // --------- getters/setters ---------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public List<JpaOrderLineEntity> getLines() { return lines; }
    public void setLines(List<JpaOrderLineEntity> lines) { this.lines = lines; }

    // --------- equals/hashCode by id ---------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaOrderEntity that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return 31; }
}
