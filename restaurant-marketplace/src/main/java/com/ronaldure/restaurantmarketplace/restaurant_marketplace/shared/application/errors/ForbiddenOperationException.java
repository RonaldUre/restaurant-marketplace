package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.errors;

/**
 * Thrown when the caller is authenticated but not authorized to perform the
 * operation.
 * Maps to HTTP 403 in the web layer.
 */
public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }

    public static ForbiddenOperationException missingRole(String requiredRole) {
        return new ForbiddenOperationException("Required role not present: " + requiredRole);
    }

    public static ForbiddenOperationException crossTenantAccess() {
        return new ForbiddenOperationException("Cross-tenant access is not allowed");
    }

    /** Thrown when an operation requires a tenant context but none is present. */
    public static ForbiddenOperationException tenantContextRequired() {
        return new ForbiddenOperationException("Tenant context is required");
    }
}
