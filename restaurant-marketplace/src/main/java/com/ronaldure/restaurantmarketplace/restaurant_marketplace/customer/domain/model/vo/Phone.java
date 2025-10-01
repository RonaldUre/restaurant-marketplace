package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object: Phone (optional)
 * - Allows E.164-like or local digits with separators.
 * - Empty represents "no phone".
 */
public final class Phone {
    private static final int MAX = 30;
    // Permissive: digits, spaces, +, -, (, )
    private static final Pattern ALLOWED = Pattern.compile("^[0-9+\\-()\\s]{0,30}$");

    private final String value;

    private Phone(String value) {
        String v = value.trim();
        if (v.length() > MAX) throw new IllegalArgumentException("Phone length must be <= " + MAX);
        if (!ALLOWED.matcher(v).matches()) throw new IllegalArgumentException("Invalid phone format");
        this.value = v;
    }

    /** Phone present. */
    public static Phone of(String raw) { return new Phone(Objects.requireNonNull(raw, "phone is required")); }

    /** No phone provided (maps nicely to NULL/empty in persistence). */
    public static Phone empty() { return new Phone(""); }

    public String value() { return value; }
    public boolean isEmpty() { return value.isEmpty(); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Phone)) return false;
        return value.equals(((Phone) o).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
