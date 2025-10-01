package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo;

import java.util.Objects;

/** Value Object: Name (trimmed, non-empty, max 120 to match schema). */
public final class Name {
    private static final int MAX = 120;
    private final String value;

    private Name(String value) {
        String v = value.trim();
        if (v.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
        if (v.length() > MAX) throw new IllegalArgumentException("Name length must be <= " + MAX);
        this.value = v;
    }

    public static Name of(String raw) { return new Name(Objects.requireNonNull(raw, "name is required")); }

    public String value() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Name)) return false;
        return value.equals(((Name) o).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
