package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo;

import java.util.Objects;

/**
 * Value Object: Category
 * - Short controlled text (e.g., "pizzas", "drinks").
 * - Kept as VO (String) for flexibility; you can evolve to enum later.
 */
public final class Category {
    private static final int MAX = 100;

    private final String value;

    private Category(String value) {
        String v = value.trim();
        if (v.isEmpty()) throw new IllegalArgumentException("Category cannot be empty");
        if (v.length() > MAX) throw new IllegalArgumentException("Category length must be <= " + MAX);
        this.value = v;
    }

    public static Category of(String raw) { return new Category(Objects.requireNonNull(raw, "category is required")); }

    public String value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        return value.equalsIgnoreCase(((Category) o).value);
    }

    @Override
    public int hashCode() { return value.toLowerCase().hashCode(); }

    @Override
    public String toString() { return value; }
}
