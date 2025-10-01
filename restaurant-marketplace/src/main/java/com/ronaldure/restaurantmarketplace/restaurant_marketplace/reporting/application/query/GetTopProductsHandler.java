package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.ports.in.GetTopProductsQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.ports.out.ReportingQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view.TopProductRow;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Handler: resolves tenant, validates dates/limit and delegates to ReportingQuery.
 */
@Service
public class GetTopProductsHandler implements GetTopProductsQuery {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 100;

    private final ReportingQuery reportingQuery;
    private final AccessControl access;
    private final CurrentTenantProvider tenants;

    public GetTopProductsHandler(ReportingQuery reportingQuery,
                                 AccessControl access,
                                 CurrentTenantProvider tenants) {
        this.reportingQuery = reportingQuery;
        this.access = access;
        this.tenants = tenants;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopProductRow> execute(GetTopProductsQueryParams params) {
        if (params == null) throw new IllegalArgumentException("params is required");
        validateRange(params.from(), params.to());
        int limit = normalizeLimit(params.limit());

        Long tenantId = resolveTenantId(params.tenantId());
        return reportingQuery.topProducts(tenantId, params.from(), params.to(), limit);
    }

    private int normalizeLimit(Integer limit) {
        int l = (limit == null) ? DEFAULT_LIMIT : limit;
        if (l <= 0) l = DEFAULT_LIMIT;
        if (l > MAX_LIMIT) l = MAX_LIMIT;
        return l;
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
