package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.web.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.query.GetDailySalesQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.query.GetOrdersStatusBreakdownQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.query.GetTopProductsQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view.DailySalesRow;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view.StatusBreakdownRow;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.view.TopProductRow;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.web.dto.request.DailySalesRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.web.dto.request.OrdersStatusRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.web.dto.request.TopProductsRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.web.dto.response.DailySalesResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.web.dto.response.StatusBreakdownResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.web.dto.response.TopProductResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Web mapper for Reporting.
 * - Maps web requests -> application query params.
 * - Maps application views -> web responses.
 *
 * Keep it free from domain/JPA types.
 */
@Component
public class ReportingWebMapper {

    // -------- Requests -> QueryParams --------

    public GetDailySalesQueryParams toParams(DailySalesRequest req) {
        if (req == null) throw new IllegalArgumentException("request is required");
        return new GetDailySalesQueryParams(req.from(), req.to(), req.tenantId());
    }

    public GetTopProductsQueryParams toParams(TopProductsRequest req) {
        if (req == null) throw new IllegalArgumentException("request is required");
        return new GetTopProductsQueryParams(req.from(), req.to(), req.limit(), req.tenantId());
    }

    public GetOrdersStatusBreakdownQueryParams toParams(OrdersStatusRequest req) {
        if (req == null) throw new IllegalArgumentException("request is required");
        return new GetOrdersStatusBreakdownQueryParams(req.from(), req.to(), req.tenantId());
    }

    // -------- Views -> Responses --------

    public DailySalesResponse toResponse(DailySalesRow row) {
        if (row == null) return null;
        return new DailySalesResponse(row.date(), row.orders(), row.totalAmount(), row.currency());
    }

    public List<DailySalesResponse> toResponse(List<DailySalesRow> rows) {
        return rows == null ? List.of() : rows.stream().map(this::toResponse).toList();
    }

    public TopProductResponse toResponse(TopProductRow row) {
        if (row == null) return null;
        return new TopProductResponse(row.productId(), row.name(), row.qty(), row.revenue(), row.currency());
    }

    public List<TopProductResponse> toTopProductsResponse(List<TopProductRow> rows) {
        return rows == null ? List.of() : rows.stream().map(this::toResponse).toList();
    }

    public StatusBreakdownResponse toResponse(StatusBreakdownRow row) {
        if (row == null) return null;
        return new StatusBreakdownResponse(row.status(), row.count());
    }

    public List<StatusBreakdownResponse> toStatusBreakdownResponse(List<StatusBreakdownRow> rows) {
        return rows == null ? List.of() : rows.stream().map(this::toResponse).toList();
    }
}
