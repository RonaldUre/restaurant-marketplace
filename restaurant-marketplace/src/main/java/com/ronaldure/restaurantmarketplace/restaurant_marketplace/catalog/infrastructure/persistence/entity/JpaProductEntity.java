// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/persistence/entity/JpaProductEntity.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA entity for the "products" table.
 * - Infra-only: no domain VOs here (pure primitives).
 * - Matches Flyway V7__init_products.sql schema exactly.
 * - Soft-delete via deletedAt (null = active).
 */
@Entity
@Table(
    name = "products",
    uniqueConstraints = {
        @UniqueConstraint(name = "ux_products_tenant_sku", columnNames = {"tenant_id", "sku"})
    },
    indexes = {
        @Index(name = "ix_products_tenant_published", columnList = "tenant_id, published"),
        @Index(name = "ix_products_tenant_category", columnList = "tenant_id, category"),
        @Index(name = "ix_products_tenant_created", columnList = "tenant_id, created_at")
    }
)
public class JpaProductEntity {

    // -------- Identity --------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------- Tenant scope --------
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    // -------- Core fields --------
    @Column(name = "sku", nullable = false, length = 64)
    private String sku;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    // MySQL TEXT
    @Column(name = "description", columnDefinition = "text")
    private String description;

    // Money
    @Column(name = "price_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal priceAmount;

    @Column(name = "price_currency", nullable = false, length = 3)
    private String priceCurrency; // ISO-4217

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "published", nullable = false)
    private boolean published;

    // -------- Audit --------
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    // -------- Constructors --------
    public JpaProductEntity() {
        // for JPA
    }

    public JpaProductEntity(Long id,
                            Long tenantId,
                            String sku,
                            String name,
                            String description,
                            BigDecimal priceAmount,
                            String priceCurrency,
                            String category,
                            boolean published,
                            Instant createdAt,
                            Instant updatedAt,
                            Instant deletedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.priceAmount = priceAmount;
        this.priceCurrency = priceCurrency;
        this.category = category;
        this.published = published;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    // -------- Getters / Setters --------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPriceAmount() { return priceAmount; }
    public void setPriceAmount(BigDecimal priceAmount) { this.priceAmount = priceAmount; }

    public String getPriceCurrency() { return priceCurrency; }
    public void setPriceCurrency(String priceCurrency) { this.priceCurrency = priceCurrency; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }

    // -------- Equality (by id) --------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaProductEntity that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
