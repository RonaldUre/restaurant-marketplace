package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo;

import java.util.Objects;

/** Human-readable restaurant name */
public final class Name {
    private final String value;

    private Name(String value) {
        this.value = Objects.requireNonNull(value, "name cannot be null").trim();
        if (this.value.isEmpty()) throw new IllegalArgumentException("name cannot be empty");
        if (this.value.length() > 120) throw new IllegalArgumentException("name too long (max 120)");
    }

    public static Name of(String value) { return new Name(value); }
    public String value() { return value; }

    @Override public String toString(){ return value; }
    @Override public boolean equals(Object o){ return (o instanceof Name n) && value.equals(n.value); }
    @Override public int hashCode(){ return value.hashCode(); }
}
