package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.ports.in.GetOrdersStatusBreakdownQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.ports.out.ReportingQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view.StatusBreakdownRow;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Handler: resolves tenant, validates date range and delegates to ReportingQuery.
 */
@Service
public class GetOrdersStatusBreakdownHandler implements GetOrdersStatusBreakdownQuery {

    private final ReportingQuery reportingQuery;
    private final AccessControl access;
    private final CurrentTenantProvider tenants;

    public GetOrdersStatusBreakdownHandler(ReportingQuery reportingQuery,
                                           AccessControl access,
                                           CurrentTenantProvider tenants) {
        this.reportingQuery = reportingQuery;
        this.access = access;
        this.tenants = tenants;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatusBreakdownRow> execute(GetOrdersStatusBreakdownQueryParams params) {
        if (params == null) throw new IllegalArgumentException("params is required");
        validateRange(params.from(), params.to());

        Long tenantId = resolveTenantId(params.tenantId());
        return reportingQuery.ordersByStatus(tenantId, params.from(), params.to());
    }

    private Long resolveTenantId(Long requestedTenantId) {
        if (requestedTenantId != null) {
            access.requireRole(Roles.SUPER_ADMIN);
            return requestedTenantId;
        }
        access.requireRole(Roles.RESTAURANT_ADMIN);
        return tenants.requireCurrent().value();
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from and to are required (LocalDate)");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("to must be >= from");
        }
    }
}
