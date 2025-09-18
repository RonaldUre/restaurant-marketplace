package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo;

import java.util.Objects;

/**
 * Value Object: ProductDescription
 * - Optional long text with sensible max length.
 * - Use Description.empty() to represent absence.
 */
public final class ProductDescription {
    private static final int MAX = 4000; // adjust if you need more

    private final String value;

    private ProductDescription(String value) {
        String v = value.trim();
        if (v.length() > MAX) throw new IllegalArgumentException("Description length must be <= " + MAX);
        this.value = v;
    }

    public static ProductDescription of(String raw) { return new ProductDescription(Objects.requireNonNull(raw, "description is required")); }

    public static ProductDescription empty() { return new ProductDescription(""); }

    public String value() { return value; }

    public boolean isEmpty() { return value.isEmpty(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDescription)) return false;
        return value.equals(((ProductDescription) o).value);
    }

    @Override
    public int hashCode() { return value.hashCode(); }

    @Override
    public String toString() { return value; }
}
