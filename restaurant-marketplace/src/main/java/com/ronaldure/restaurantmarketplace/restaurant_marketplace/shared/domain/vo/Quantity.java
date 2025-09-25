package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo;

import java.util.Objects;

/**
 * Value Object: Quantity
 * - Non-negative integer.
 * - Immutable; provides safe arithmetic helpers.
 */
public final class Quantity {
    private final int value;

    private Quantity(int value) {
        if (value < 0) throw new IllegalArgumentException("Quantity must be >= 0");
        this.value = value;
    }

    public static Quantity of(int v) { return new Quantity(v); }

    public static Quantity zero() { return new Quantity(0); }

    public int value() { return value; }

    /** Returns a new Quantity = this + other (overflow-safe for practical ranges). */
    public Quantity add(Quantity other) {
        Objects.requireNonNull(other, "other is required");
        long sum = (long) this.value + (long) other.value;
        if (sum > Integer.MAX_VALUE) throw new ArithmeticException("Quantity overflow");
        return new Quantity((int) sum);
    }

    /**
     * Tries to subtract; returns null if not enough.
     * Useful for "check then apply" flows.
     */
    public Quantity trySubtract(Quantity other) {
        Objects.requireNonNull(other, "other is required");
        int diff = this.value - other.value;
        return diff < 0 ? null : new Quantity(diff);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quantity)) return false;
        return value == ((Quantity) o).value;
    }
    @Override public int hashCode() { return Integer.hashCode(value); }
    @Override public String toString() { return String.valueOf(value); }
}
