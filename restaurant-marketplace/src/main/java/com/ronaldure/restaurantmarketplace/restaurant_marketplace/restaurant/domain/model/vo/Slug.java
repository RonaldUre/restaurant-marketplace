package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo;

import java.util.Objects;
import java.util.regex.Pattern;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.validation.Patterns;

/** URL-friendly unique slug */
public final class Slug {
    private static final Pattern P = Pattern.compile(Patterns.SLUG); // kebab-case // kebab-case
    private final String value;

    private Slug(String value) {
        this.value = Objects.requireNonNull(value, "slug cannot be null").trim();
        if (this.value.length() > 140) throw new IllegalArgumentException("slug too long (max 140)");
        if (!P.matcher(this.value).matches()) throw new IllegalArgumentException("invalid slug format");
    }

    public static Slug of(String value){ return new Slug(value); }
    public String value(){ return value; }

    @Override public String toString(){ return value; }
    @Override public boolean equals(Object o){ return (o instanceof Slug s) && value.equals(s.value); }
    @Override public int hashCode(){ return value.hashCode(); }
}
