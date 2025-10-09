package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;

import java.util.Objects;

/** Wrapper fino; puedes usar UserId directamente si prefieres. */
public final class CustomerId {
    private final long value;

    private CustomerId(long value) {
        if (value <= 0) throw new IllegalArgumentException("CustomerId must be > 0");
        this.value = value;
    }

    public static CustomerId of(long value){ return new CustomerId(value); }
    public static CustomerId from(UserId userId){ return new CustomerId(Long.parseLong(userId.value())); }
    public long value(){ return value; }

    @Override public boolean equals(Object o){ return (o instanceof CustomerId) && ((CustomerId)o).value == value; }
    @Override public int hashCode(){ return Objects.hash(value); }
    @Override public String toString(){ return String.valueOf(value); }
}
