package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors.OrderNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.GetOrderAdminQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.AdminOrderDetailQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetOrderAdminHandler implements GetOrderAdminQuery {

    private final AdminOrderDetailQuery adminDetailQuery;
    private final CurrentTenantProvider tenantProvider;
    private final AccessControl accessControl;

    public GetOrderAdminHandler(AdminOrderDetailQuery adminDetailQuery,
                                CurrentTenantProvider tenantProvider,
                                AccessControl accessControl) {
        this.adminDetailQuery = adminDetailQuery;
        this.tenantProvider = tenantProvider;
        this.accessControl = accessControl;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailView get(Long orderId) {
        accessControl.requireRole(Roles.RESTAURANT_ADMIN);
        TenantId tenantId = tenantProvider.requireCurrent();

        return adminDetailQuery
                .findById(tenantId, orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
