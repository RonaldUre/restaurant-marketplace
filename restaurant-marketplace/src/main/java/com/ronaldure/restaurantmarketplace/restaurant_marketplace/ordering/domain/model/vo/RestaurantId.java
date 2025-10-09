package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;

/** Alias de TenantId para semántica en Ordering; puedes usar TenantId directamente. */
public final class RestaurantId {
    private final long value;

    private RestaurantId(long value) {
        if (value <= 0) throw new IllegalArgumentException("RestaurantId must be > 0");
        this.value = value;
    }

    public static RestaurantId of(long value){ return new RestaurantId(value); }
    public static RestaurantId from(TenantId tenantId){ return new RestaurantId(tenantId.value()); }
    public long value(){ return value; }

    @Override public boolean equals(Object o){ return (o instanceof RestaurantId) && ((RestaurantId)o).value == value; }
    @Override public int hashCode(){ return Long.hashCode(value); }
    @Override public String toString(){ return String.valueOf(value); }
}
