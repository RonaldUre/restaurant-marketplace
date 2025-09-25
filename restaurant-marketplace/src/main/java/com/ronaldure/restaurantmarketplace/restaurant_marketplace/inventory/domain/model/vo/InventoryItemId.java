package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.domain.model.vo;

/** Technical identity for InventoryItem (DB-generated). */
public final class InventoryItemId {
    private final long value;

    private InventoryItemId(long value) {
        if (value <= 0) throw new IllegalArgumentException("InventoryItemId must be > 0");
        this.value = value;
    }

    public static InventoryItemId of(long v) { return new InventoryItemId(v); }
    public long value() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryItemId)) return false;
        return value == ((InventoryItemId) o).value;
    }
    @Override public int hashCode() { return Long.hashCode(value); }
    @Override public String toString() { return String.valueOf(value); }
}
