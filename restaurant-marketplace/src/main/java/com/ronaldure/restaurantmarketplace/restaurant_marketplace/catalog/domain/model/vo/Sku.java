package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object: Sku
 * - Unique per tenant (enforced at repository/DB level).
 * - Normalized to upper-case; restricts allowed characters.
 */
public final class Sku {
    private static final Pattern ALLOWED = Pattern.compile("^[A-Z0-9._-]{1,64}$");

    private final String value;

    private Sku(String value) {
        String normalized = value.trim().toUpperCase();
        if (normalized.isEmpty()) throw new IllegalArgumentException("SKU cannot be empty");
        if (normalized.length() > 64) throw new IllegalArgumentException("SKU length must be <= 64");
        if (!ALLOWED.matcher(normalized).matches()) {
            throw new IllegalArgumentException("SKU allows A-Z, 0-9, dot, dash, underscore");
        }
        this.value = normalized;
    }

    public static Sku of(String raw) { return new Sku(Objects.requireNonNull(raw, "sku is required")); }

    public String value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sku)) return false;
        return value.equals(((Sku) o).value);
    }

    @Override
    public int hashCode() { return value.hashCode(); }

    @Override
    public String toString() { return value; }
}
