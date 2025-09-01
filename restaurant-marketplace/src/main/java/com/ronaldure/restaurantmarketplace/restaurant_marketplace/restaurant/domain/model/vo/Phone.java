package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo;

import java.util.Objects;

/** Simple phone holder; extend with E.164 if needed */
public final class Phone {
    private final String value;

    private Phone(String value) {
        this.value = Objects.requireNonNull(value, "phone cannot be null").trim();
        if (this.value.length() > 30) throw new IllegalArgumentException("phone too long (max 30)");
    }

    public static Phone of(String value){ return new Phone(value); }
    public String value(){ return value; }

    @Override public String toString(){ return value; }
    @Override public boolean equals(Object o){ return (o instanceof Phone p) && value.equals(p.value); }
    @Override public int hashCode(){ return value.hashCode(); }
}
