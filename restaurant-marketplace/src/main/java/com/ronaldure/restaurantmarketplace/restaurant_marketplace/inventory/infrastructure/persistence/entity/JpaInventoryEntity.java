// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/inventory/infrastructure/persistence/entity/JpaInventoryEntity.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * JPA entity for "inventory".
 * - available NULL => unlimited
 * - Optimistic locking via @Version
 * - Matches Flyway V9__init_inventory.sql
 */
@Entity
@Table(
    name = "inventory",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_inventory_tenant_product", columnNames = {"tenant_id", "product_id"})
    },
    indexes = {
        @Index(name = "idx_inventory_tenant", columnList = "tenant_id"),
        @Index(name = "idx_inventory_product", columnList = "product_id"),
        @Index(name = "idx_inventory_tenant_available", columnList = "tenant_id, available")
    }
)
public class JpaInventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tenant scope
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** NULL => unlimited; otherwise >= 0 */
    @Column(name = "available")
    private Integer available;

    /** Never null; >= 0 */
    @Column(name = "reserved", nullable = false)
    private Integer reserved = 0;

    /** Optimistic locking version */
    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // --- JPA only ---
    public JpaInventoryEntity() {}

    public JpaInventoryEntity(Long id,
                              Long tenantId,
                              Long productId,
                              Integer available,
                              Integer reserved,
                              Integer version,
                              Instant createdAt,
                              Instant updatedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.productId = productId;
        this.available = available;
        this.reserved = reserved;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- Getters / Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getAvailable() { return available; }
    public void setAvailable(Integer available) { this.available = available; }

    public Integer getReserved() { return reserved; }
    public void setReserved(Integer reserved) { this.reserved = reserved; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // Equality by id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaInventoryEntity that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }
}
