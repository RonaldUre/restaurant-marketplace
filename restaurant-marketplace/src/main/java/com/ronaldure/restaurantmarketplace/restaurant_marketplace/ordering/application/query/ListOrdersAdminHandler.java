package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.ListOrdersAdminQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.AdminOrderQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListOrdersAdminHandler implements ListOrdersAdminQuery {

    private final AdminOrderQuery adminOrderQuery;
    private final CurrentTenantProvider tenantProvider;
    private final AccessControl accessControl;

    public ListOrdersAdminHandler(AdminOrderQuery adminOrderQuery,
                                  CurrentTenantProvider tenantProvider,
                                  AccessControl accessControl) {
        this.adminOrderQuery = adminOrderQuery;
        this.tenantProvider = tenantProvider;
        this.accessControl = accessControl;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderCardView> list(ListOrdersAdminQueryParams params) {
        accessControl.requireRole(Roles.RESTAURANT_ADMIN);
        TenantId tenantId = tenantProvider.requireCurrent();

        PageRequest page = new PageRequest(params.page(), params.size());
        return adminOrderQuery.list(tenantId, params, page);
    }
}
