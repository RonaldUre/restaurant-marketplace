package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.model.vo.InventoryItemId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.ProductId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Quantity;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate Root: InventoryItem
 *
 * Invariants:
 * - If available == null => unlimited; reservations are NO-OP (successful).
 * - reserved >= 0.
 * - If available != null => reserved <= available.
 *
 * Notes:
 * - Pure domain (no JPA). Technical id is optional before persistence.
 * - Business identity is (tenantId, productId).
 */
public final class InventoryItem {

    // Technical identity (nullable before persistence)
    private InventoryItemId id;

    // Business identity
    private final TenantId tenantId;
    private final ProductId productId;

    /**
     * available == null => unlimited stock
     * Otherwise, available >= 0
     */
    private Integer available; // boxed to allow null
    private Quantity reserved; // always non-null (>= 0)

    // Optional timestamps for parity with persistence model
    private final Instant createdAt;
    private Instant updatedAt;

    private InventoryItem(
            InventoryItemId id,
            TenantId tenantId,
            ProductId productId,
            Integer available,
            Quantity reserved,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId is required");
        this.productId = Objects.requireNonNull(productId, "productId is required");
        this.available = available; // can be null (unlimited)
        this.reserved = Objects.requireNonNull(reserved, "reserved is required");
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();

        validateInvariants();
    }

    /** Factory: limited stock */
    public static InventoryItem createLimited(TenantId tenantId, ProductId productId, Quantity initialAvailable) {
        Objects.requireNonNull(initialAvailable, "initialAvailable is required");
        return new InventoryItem(
                null,
                tenantId,
                productId,
                initialAvailable.value(),
                Quantity.zero(),
                Instant.now(),
                Instant.now());
    }

    /** Factory: unlimited stock */
    public static InventoryItem createUnlimited(TenantId tenantId, ProductId productId) {
        return new InventoryItem(
                null,
                tenantId,
                productId,
                null, // unlimited
                Quantity.zero(),
                Instant.now(),
                Instant.now());
    }

    /** Rehydrate from persistence */
    public static InventoryItem rehydrate(
            InventoryItemId id,
            TenantId tenantId,
            ProductId productId,
            Integer available,
            int reserved,
            Instant createdAt,
            Instant updatedAt) {
        return new InventoryItem(
                Objects.requireNonNull(id, "id is required"),
                tenantId,
                productId,
                available,
                Quantity.of(reserved),
                createdAt,
                updatedAt);
    }

    /** Assign technical id once */
    public void assignId(InventoryItemId id) {
        if (this.id != null)
            throw new IllegalStateException("InventoryItem id already assigned");
        this.id = Objects.requireNonNull(id, "id is required");
    }

    /** Admin-only: adjust available by delta (limited only). */
    public void adjust(int delta) {

        // Evita NPE por unboxing accidental
        final Integer availBoxed = this.available;
        if (availBoxed == null) {
            throw new IllegalStateException("Cannot adjust unlimited stock");
        }

        int current = availBoxed.intValue();
        
        long next = (long) current + (long) delta;

        if (next < 0)
            throw new IllegalStateException("Available cannot be negative");
        if (next > Integer.MAX_VALUE)
            throw new IllegalStateException("Available overflow");
        if (next < this.reserved.value())
            throw new IllegalStateException("Available cannot be less than reserved");
        this.available = (int) next;
        touch();
        validateInvariants();
    }

    /**
     * Reserve qty. Unlimited => NO-OP (success). Limited => check (available -
     * reserved >= qty).
     */
    public void reserve(Quantity qty) {
        Objects.requireNonNull(qty, "qty is required");
        if (isUnlimited()) {
            // No reservations tracked when unlimited
            return;
        }
        Quantity diff = Quantity.of(this.available).trySubtract(this.reserved);
        if (diff == null || diff.value() < qty.value()) {
            throw new IllegalStateException("Insufficient available to reserve");
        }
        this.reserved = this.reserved.add(qty);
        touch();
        validateInvariants();
    }

    /**
     * Release qty from reserved. Unlimited => NO-OP. Limited => reserved >= qty.
     */
    public void release(Quantity qty) {
        Objects.requireNonNull(qty, "qty is required");
        if (isUnlimited()) {
            return;
        }
        Quantity next = this.reserved.trySubtract(qty);
        if (next == null) {
            throw new IllegalStateException("Cannot release more than reserved");
        }
        this.reserved = next;
        touch();
        validateInvariants();
    }

    public void switchToLimited(Quantity initialAvailable) {
        Objects.requireNonNull(initialAvailable, "initialAvailable is required");
        if (!isUnlimited())
            return; // idempotente: ya es limitado
        // reserved siempre >= 0. Si había reservas (no debería en ilimitado), igual es
        // seguro:
        if (initialAvailable.value() < reserved.value()) {
            throw new IllegalStateException("initialAvailable cannot be less than reserved");
        }
        this.available = initialAvailable.value();
        touch();
        validateInvariants();
    }

    public void switchToUnlimited() {
        if (isUnlimited())
            return; // idempotente
        if (reserved.value() > 0) {
            throw new IllegalStateException("Cannot switch to unlimited while reserved > 0");
        }
        this.available = null; // ilimitado
        touch();
        validateInvariants();
    }

    /**
     * Confirm qty: applies to limited only.
     * reserved -= qty; available -= qty. Both must stay >= 0 and reserved <=
     * available.
     * Unlimited => NO-OP (success).
     */
    public void confirm(Quantity qty) {
        Objects.requireNonNull(qty, "qty is required");
        if (isUnlimited()) {
            return;
        }
        // reserved - qty
        Quantity nextReserved = this.reserved.trySubtract(qty);
        if (nextReserved == null)
            throw new IllegalStateException("Not enough reserved to confirm");

        // available - qty
        int nextAvailable = this.available - qty.value();
        if (nextAvailable < 0)
            throw new IllegalStateException("Available cannot go negative");

        this.reserved = nextReserved;
        this.available = nextAvailable;
        touch();
        validateInvariants();
    }

    private boolean isUnlimited() {
        return this.available == null;
    }

    private void validateInvariants() {
        if (this.reserved.value() < 0)
            throw new IllegalStateException("Reserved < 0");
        if (this.available != null) {
            if (this.available < 0)
                throw new IllegalStateException("Available < 0");
            if (this.reserved.value() > this.available) {
                throw new IllegalStateException("Reserved cannot exceed available");
            }
        }
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    // Getters
    public InventoryItemId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public ProductId productId() {
        return productId;
    }

    public Integer available() {
        return available;
    } // null => unlimited

    public Quantity reserved() {
        return reserved;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    // Equality by business identity
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof InventoryItem))
            return false;
        InventoryItem other = (InventoryItem) o;
        return tenantId.equals(other.tenantId) && productId.equals(other.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, productId);
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "id=" + (id != null ? id.value() : "null") +
                ", tenantId=" + tenantId +
                ", productId=" + productId +
                ", available=" + (available == null ? "UNLIMITED" : available) +
                ", reserved=" + reserved +
                '}';
    }
}
