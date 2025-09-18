package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo;

import java.util.Objects;

/**
 * Value Object: ProductId
 * - Strongly-typed identity for Product aggregate.
 * - Wraps a database-generated long id.
 */
public final class ProductId {
    private final long value;

    private ProductId(long value) {
        if (value <= 0) throw new IllegalArgumentException("ProductId must be > 0");
        this.value = value;
    }

    public static ProductId of(long value) { return new ProductId(value); }

    public long value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductId)) return false;
        ProductId other = (ProductId) o;
        return value == other.value;
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return String.valueOf(value); }
}
