package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class AuthenticatedUser {
    private final UserId userId;
    private final Set<Role> roles;
    private final TenantId tenantId; // null when not applicable (e.g., CUSTOMER in public context or SUPER_ADMIN)

    public AuthenticatedUser(UserId userId, Set<Role> roles, TenantId tenantId) {
        this.userId = Objects.requireNonNull(userId, "userId");
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("roles cannot be empty");
        }
        this.roles = Collections.unmodifiableSet(EnumSet.copyOf(roles));
        this.tenantId = tenantId; // may be null
    }

    public UserId userId() { return userId; }
    public Set<Role> roles() { return roles; }
    public Optional<TenantId> tenantId() { return Optional.ofNullable(tenantId); }

    public boolean hasRole(Role role) { return roles.contains(role); }
    public boolean isSuperAdmin() { return hasRole(Role.SUPER_ADMIN); }

    /** In admin routes a tenant must exist; otherwise throws an IllegalStateException. */
    public TenantId requireTenant() {
        return Optional.ofNullable(tenantId)
            .orElseThrow(() -> new IllegalStateException("TenantId is required in admin context"));
    }
}