package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo;

import java.util.Objects;

public final class OrderId {
    private final long value;

    private OrderId(long value) {
        if (value <= 0) throw new IllegalArgumentException("OrderId must be > 0");
        this.value = value;
    }

    public static OrderId of(long value) { return new OrderId(value); }
    public long value() { return value; }

    @Override public boolean equals(Object o){ return (o instanceof OrderId) && ((OrderId)o).value == value; }
    @Override public int hashCode(){ return Objects.hash(value); }
    @Override public String toString(){ return String.valueOf(value); }
}
