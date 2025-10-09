package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo;

import java.util.Objects;

/**
 * Value Object: ProductName
 * - Human-friendly name.
 * - Trimmed; prevents empty and overly long names.
 */
public final class ProductName {
    private static final int MAX = 255;

    private final String value;

    private ProductName(String value) {
        String v = value.trim();
        if (v.isEmpty()) throw new IllegalArgumentException("Product name cannot be empty");
        if (v.length() > MAX) throw new IllegalArgumentException("Product name length must be <= " + MAX);
        this.value = v;
    }

    public static ProductName of(String raw) { return new ProductName(Objects.requireNonNull(raw, "name is required")); }

    public String value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductName)) return false;
        return value.equals(((ProductName) o).value);
    }

    @Override
    public int hashCode() { return value.hashCode(); }

    @Override
    public String toString() { return value; }
}
