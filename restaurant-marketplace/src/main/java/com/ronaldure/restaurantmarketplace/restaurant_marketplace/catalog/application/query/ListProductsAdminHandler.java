package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in.ListProductsAdminQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out.AdminProductQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListProductsAdminHandler implements ListProductsAdminQuery {

    private final AdminProductQuery adminProductQuery;
    private final CurrentTenantProvider tenantProvider;
    private final AccessControl accessControl;

    public ListProductsAdminHandler(AdminProductQuery adminProductQuery,
                                    CurrentTenantProvider tenantProvider,
                                    AccessControl accessControl) {
        this.adminProductQuery = adminProductQuery;
        this.tenantProvider = tenantProvider;
        this.accessControl = accessControl;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductAdminCardView> list(ListProductsAdminQueryParams params, PageRequest page) {
        // Authorization: only RESTAURANT_ADMIN (or SUPER_ADMIN if decides to reuse)
        accessControl.requireRole("RESTAURANT_ADMIN");

        // Tenant from JWT (never trust request)
        TenantId tenantId = tenantProvider.requireCurrent();

        // Delegate to query adapter (scoped to tenant)
        return adminProductQuery.list(tenantId, params, page);
    }
}
