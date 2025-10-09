package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo;

import java.util.Objects;

/** Value Object: CustomerId (DB-generated long > 0). */
public final class CustomerId {
    private final long value;

    private CustomerId(long value) {
        if (value <= 0) throw new IllegalArgumentException("CustomerId must be > 0");
        this.value = value;
    }

    public static CustomerId of(long value) { return new CustomerId(value); }

    public long value() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerId)) return false;
        return value == ((CustomerId) o).value;
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return String.valueOf(value); }
}
