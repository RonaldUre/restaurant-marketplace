package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.errors.ProductNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in.GetProductAdminQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out.AdminProductDetailQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetProductAdminHandler implements GetProductAdminQuery {

    private final AdminProductDetailQuery adminDetailQuery;
    private final CurrentTenantProvider tenantProvider;
    private final AccessControl accessControl;

    public GetProductAdminHandler(AdminProductDetailQuery adminDetailQuery,
                                  CurrentTenantProvider tenantProvider,
                                  AccessControl accessControl) {
        this.adminDetailQuery = adminDetailQuery;
        this.tenantProvider = tenantProvider;
        this.accessControl = accessControl;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductAdminDetailView get(Long productId) {
        // Authorization
        accessControl.requireRole(Roles.RESTAURANT_ADMIN);

        // Tenant scope
        TenantId tenantId = tenantProvider.requireCurrent();

        // Fetch projection; 404 if not found in this tenant
        return adminDetailQuery
                .findById(tenantId, productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}
