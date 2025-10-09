package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;

import java.util.Objects;
import java.util.Optional;

/**
 * Request-scoped holder for tenant/user identity, backed by ThreadLocal.
 * Set only after token validation on admin routes.
 * Do NOT set tenant for public or platform routes.
 */
public final class TenantContext {

    private record State(UserId userId, Long tenantId) {}

    private static final ThreadLocal<State> HOLDER = new ThreadLocal<>();

    private TenantContext() { }

    public static void set(UserId userId, Long tenantId) {
        Objects.requireNonNull(userId, "userId");
        HOLDER.set(new State(userId, tenantId));
    }

    public static Optional<UserId> getUser() {
        State s = HOLDER.get();
        return s == null ? Optional.empty() : Optional.of(s.userId());
    }

    public static Optional<Long> getTenantId() {
        State s = HOLDER.get();
        return s == null ? Optional.empty() : Optional.ofNullable(s.tenantId());
    }

    public static UserId getUserOrThrow() {
        return getUser().orElseThrow(() -> new IllegalStateException("UserId not present in context"));
    }

    public static Long getTenantIdOrThrow() {
        return getTenantId().orElseThrow(() -> new IllegalStateException("TenantId not present in admin context"));
    }

    public static void clear() {
        HOLDER.remove();
    }
}
