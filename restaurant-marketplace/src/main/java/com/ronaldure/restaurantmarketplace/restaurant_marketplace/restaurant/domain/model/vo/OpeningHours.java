package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo;

import java.util.Objects;

/** JSON-backed opening hours; validate structure later if needed */
public final class OpeningHours {
    private final String json; // e.g., {"mon":[["09:00","18:00"]],...}

    private OpeningHours(String json) {
        this.json = Objects.requireNonNull(json, "opening hours json cannot be null").trim();
        if (this.json.isEmpty()) throw new IllegalArgumentException("opening hours cannot be empty");
        // Optionally: add JSON schema/format validation later
    }

    public static OpeningHours of(String json){ return new OpeningHours(json); }
    public String json(){ return json; }

    @Override public String toString(){ return json; }
    @Override public boolean equals(Object o){ return (o instanceof OpeningHours oh) && json.equals(oh.json); }
    @Override public int hashCode(){ return json.hashCode(); }
}
