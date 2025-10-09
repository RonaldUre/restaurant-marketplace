package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object: Email
 * - Lower-cased canonical form.
 * - Basic RFC-ish validation; database enforces uniqueness.
 */
public final class Email {
    private static final int MAX = 255;
    // Simplified email pattern; stricter rules can live in application layer if needed.
    private static final Pattern SIMPLE = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final String value;

    private Email(String value) {
        String v = value.trim().toLowerCase();
        if (v.isEmpty()) throw new IllegalArgumentException("Email cannot be empty");
        if (v.length() > MAX) throw new IllegalArgumentException("Email length must be <= " + MAX);
        if (!SIMPLE.matcher(v).matches()) throw new IllegalArgumentException("Invalid email format");
        this.value = v;
    }

    public static Email of(String raw) { return new Email(Objects.requireNonNull(raw, "email is required")); }

    public String value() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        return value.equals(((Email) o).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
