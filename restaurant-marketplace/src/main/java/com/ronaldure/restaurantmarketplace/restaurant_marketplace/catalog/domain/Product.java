package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate Root: Product
 *
 * Responsibilities:
 * - Enforces invariants for catalog products within a single tenant
 * (restaurant).
 * - Controls state transitions (create, update details, publish/unpublish).
 * - Provides a single entry point to mutate the product state.
 *
 * Notes:
 * - No JPA annotations here; this is pure domain (hexagonal architecture).
 * - Business identity uses (tenantId, sku). Technical identity (id) can be
 * assigned later.
 */
public final class Product {

    // Technical identity (assigned by persistence)
    private ProductId id; // nullable before persistence

    // Business identity
    private final TenantId tenantId;
    private final Sku sku;

    // Attributes
    private ProductName name;
    private ProductDescription description;
    private Category category;
    private Money price;
    private boolean published;

    // Timestamps (optional in domain; kept for convenience/consistency)
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt; // null means active

    private Product(
            ProductId id,
            TenantId tenantId,
            Sku sku,
            ProductName name,
            ProductDescription description,
            Category category,
            Money price,
            boolean published,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        this.id = id;
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId is required");
        this.sku = Objects.requireNonNull(sku, "sku is required");
        this.name = Objects.requireNonNull(name, "name is required");
        this.description = Objects.requireNonNull(description, "description is required");
        this.category = Objects.requireNonNull(category, "category is required");
        this.price = Objects.requireNonNull(price, "price is required");
        this.published = published;
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
        this.updatedAt = Objects.requireNonNullElseGet(updatedAt, Instant::now);
        this.deletedAt = deletedAt;

        validateInvariants();
    }

    /** Factory method to create a new Product in an unpublished state. */
    public static Product create(
            TenantId tenantId,
            Sku sku,
            ProductName name,
            ProductDescription description,
            Category category,
            Money price) {
        return new Product(
                null, // id assigned by persistence later
                tenantId,
                sku,
                name,
                description,
                category,
                price,
                false, // published
                Instant.now(),
                Instant.now(),
                null // deletedAt
        );
    }

    // dentro de Product
    public static Product rehydrate(
            ProductId id,
            TenantId tenantId,
            Sku sku,
            ProductName name,
            ProductDescription description,
            Category category,
            Money price,
            boolean published,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        // No tocar timestamps ni published aquí.
        return new Product(
                Objects.requireNonNull(id, "id is required"),
                tenantId,
                sku,
                name,
                description,
                category,
                price,
                published,
                createdAt,
                updatedAt,
                deletedAt);
    }

    /** Assigns the technical ID after persistence. Should be called only once. */
    public void assignId(ProductId id) {
        if (this.id != null) {
            throw new IllegalStateException("Product id is already assigned");
        }
        this.id = Objects.requireNonNull(id, "id is required");
    }

    /** Update basic, mutable attributes in one atomic operation. */
    public void updateDetails(
            ProductName name,
            ProductDescription description,
            Category category,
            Money price) {
        this.name = Objects.requireNonNull(name, "name is required");
        this.description = Objects.requireNonNull(description, "description is required");
        this.category = Objects.requireNonNull(category, "category is required");
        this.price = Objects.requireNonNull(price, "price is required");
        validateInvariants();
        touch();
    }

    /** Publish the product to be visible in public catalog. */
    public void publish() {
        if (isArchived()) {
            throw new IllegalStateException("Archived products cannot be published");
        }
        if (name == null || name.value().isBlank()) {
            throw new IllegalStateException("Cannot publish product without a valid name");
        }
        if (price.isZero()) {
            // business rule: avoid publishing free-priced items unless explicitly decided
            // otherwise
            throw new IllegalStateException("Cannot publish product with zero price");
        }
        this.published = true;
        touch();
    }

    /** Unpublish the product (hide from public catalog). */
    public void unpublish() {
        this.published = false;
        touch();
    }

    /** Optional: soft-delete semantics (aligns nicely with deleted_at column). */
    public void archive() {
        if (!isArchived()) {
            this.deletedAt = Instant.now();
            this.published = false;
            touch();
        }
    }

    public boolean isArchived() {
        return this.deletedAt != null;
    }

    private void validateInvariants() {
        // Ensures the aggregate is in a coherent state at all times.
        // Money/VOs already validate themselves; here we can add cross-field checks.
        if (tenantId == null)
            throw new IllegalStateException("tenantId must be present");
        if (sku == null)
            throw new IllegalStateException("sku must be present");
        if (name == null)
            throw new IllegalStateException("name must be present");
        if (price == null)
            throw new IllegalStateException("price must be present");

        // Example cross-rule: prevent empty category if your business requires it
        // (already enforced in VO)
        if (category == null)
            throw new IllegalStateException("category must be present");
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    // -------- Getters (no setters; aggregate controls all mutations) --------

    public ProductId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public Sku sku() {
        return sku;
    }

    public ProductName name() {
        return name;
    }

    public ProductDescription description() {
        return description;
    }

    public Category category() {
        return category;
    }

    public Money price() {
        return price;
    }

    public boolean published() {
        return published;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Instant deletedAt() {
        return deletedAt;
    }

    // -------- Equality & HashCode --------
    // Business identity before persistence: (tenantId, sku)
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Product))
            return false;
        Product other = (Product) o;
        return tenantId.equals(other.tenantId) && sku.equals(other.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, sku);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + (id != null ? id.value() : "null") +
                ", tenantId=" + tenantId +
                ", sku=" + sku +
                ", name=" + name +
                ", price=" + price +
                ", published=" + published +
                '}';
    }
}
