package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security;

public record UserId(String value) {
    public UserId {
        if (value == null) throw new IllegalArgumentException("UserId cannot be null");
        String v = value.trim();
        if (v.isEmpty()) throw new IllegalArgumentException("UserId cannot be blank");
        if (v.length() > 128) throw new IllegalArgumentException("UserId length must be <= 128");
        value = v;
    }

    /** Para IDs alfanuméricos como el sub del JWT. */
    public static UserId of(String raw) {
        return new UserId(raw);
    }

    /** Conveniente cuando tienes IDs numéricos. */
    public static UserId of(Long raw) {
        if (raw == null) throw new IllegalArgumentException("UserId cannot be null");
        return new UserId(Long.toString(raw));
    }
    public static UserId of(long raw) {
        return new UserId(Long.toString(raw));
    }

    /** Útil si el sub es un UUID. */
    public static UserId of(java.util.UUID uuid) {
        if (uuid == null) throw new IllegalArgumentException("UserId cannot be null");
        return new UserId(uuid.toString());
    }

    @Override public String toString() { return value; }
}
