package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo;

import java.util.Objects;
import java.util.regex.Pattern;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.validation.Patterns;

/** Optional email, but if present must be valid */
public final class Email {
    private static final Pattern P = Pattern.compile(Patterns.EMAIL);
    private final String value; // may be null? -> prefer absent by not creating instance when null

    private Email(String value) {
        this.value = Objects.requireNonNull(value, "email cannot be null").trim();
        if (this.value.length() > 255) throw new IllegalArgumentException("email too long (max 255)");
        if (!P.matcher(this.value).matches()) throw new IllegalArgumentException("invalid email");
    }

    public static Email of(String value){ return new Email(value); }
    public String value(){ return value; }

    @Override public String toString(){ return value; }
    @Override public boolean equals(Object o){ return (o instanceof Email e) && value.equalsIgnoreCase(e.value); }
    @Override public int hashCode(){ return value.toLowerCase().hashCode(); }
}
