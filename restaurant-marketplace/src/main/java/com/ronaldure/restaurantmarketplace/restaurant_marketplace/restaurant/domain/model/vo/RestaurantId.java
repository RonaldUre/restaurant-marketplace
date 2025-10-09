package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo;

import java.util.Objects;

/** Immutable identifier for Restaurant */
public final class RestaurantId {
    private final Long value;

    private RestaurantId(Long value) {
        this.value = Objects.requireNonNull(value, "id cannot be null");
        if (value <= 0) throw new IllegalArgumentException("id must be positive");
    }

    public static RestaurantId of(Long value) { return new RestaurantId(value); }
    public Long value() { return value; }

    @Override public String toString() { return String.valueOf(value); }
    @Override public boolean equals(Object o){ return (o instanceof RestaurantId r) && value.equals(r.value); }
    @Override public int hashCode(){ return value.hashCode(); }
}
