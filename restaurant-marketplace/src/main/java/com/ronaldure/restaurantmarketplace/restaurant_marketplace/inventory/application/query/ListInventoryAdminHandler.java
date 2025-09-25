// src/main/java/.../inventory/application/query/ListInventoryAdminHandler.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.in.ListInventoryAdminQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out.InventoryAdminQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.view.InventoryAdminItemView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListInventoryAdminHandler implements ListInventoryAdminQuery {

    private final InventoryAdminQuery adminQuery;
    private final CurrentTenantProvider tenantProvider;
    private final AccessControl accessControl;

    public ListInventoryAdminHandler(InventoryAdminQuery adminQuery,
                                     CurrentTenantProvider tenantProvider,
                                     AccessControl accessControl) {
        this.adminQuery = adminQuery;
        this.tenantProvider = tenantProvider;
        this.accessControl = accessControl;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InventoryAdminItemView> list(ListInventoryAdminQueryParams params) {
        accessControl.requireRole(Roles.RESTAURANT_ADMIN);
        TenantId tenantId = tenantProvider.requireCurrent();

        // Build PageRequest inside the handler (standard across modules)
        PageRequest page = new PageRequest(params.page(), params.size());

        return adminQuery.list(tenantId, params, page);
    }
}
