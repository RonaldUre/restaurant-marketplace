package com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.ports.in.GetDailySalesQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.ports.in.GetOrdersStatusBreakdownQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.application.ports.in.GetTopProductsQuery;
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
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.reporting.infrastructure.web.mapper.ReportingWebMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de reporting para administradores de restaurante.
 * Importante: se IGNORA cualquier tenantId recibido y se usa el del JWT
 * (los handlers resuelven el tenant a través de CurrentTenantProvider).
 */
@RestController
@RequestMapping("/admin/reporting")
@Validated
public class AdminReportingController {

    private final GetDailySalesQuery dailySalesQuery;
    private final GetTopProductsQuery topProductsQuery;
    private final GetOrdersStatusBreakdownQuery statusBreakdownQuery;
    private final ReportingWebMapper webMapper;

    public AdminReportingController(GetDailySalesQuery dailySalesQuery,
                                    GetTopProductsQuery topProductsQuery,
                                    GetOrdersStatusBreakdownQuery statusBreakdownQuery,
                                    ReportingWebMapper webMapper) {
        this.dailySalesQuery = dailySalesQuery;
        this.topProductsQuery = topProductsQuery;
        this.statusBreakdownQuery = statusBreakdownQuery;
        this.webMapper = webMapper;
    }

    // GET /admin/reporting/sales/daily → 200 OK
    @GetMapping("/sales/daily")
    public ResponseEntity<List<DailySalesResponse>> dailySales(@Valid @ModelAttribute DailySalesRequest req) {
        // Forzar tenantId = null para evitar exigir SUPER_ADMIN si el cliente lo envía
        var params = new GetDailySalesQueryParams(req.from(), req.to(), null);
        List<DailySalesRow> rows = dailySalesQuery.execute(params);
        return ResponseEntity.ok(webMapper.toResponse(rows));
    }

    // GET /admin/reporting/top-products → 200 OK
    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductResponse>> topProducts(@Valid @ModelAttribute TopProductsRequest req) {
        var params = new GetTopProductsQueryParams(req.from(), req.to(), req.limit(), null);
        List<TopProductRow> rows = topProductsQuery.execute(params);
        return ResponseEntity.ok(webMapper.toTopProductsResponse(rows));
    }

    // GET /admin/reporting/orders/status → 200 OK
    @GetMapping("/orders/status")
    public ResponseEntity<List<StatusBreakdownResponse>> ordersStatus(@Valid @ModelAttribute OrdersStatusRequest req) {
        var params = new GetOrdersStatusBreakdownQueryParams(req.from(), req.to(), null);
        List<StatusBreakdownRow> rows = statusBreakdownQuery.execute(params);
        return ResponseEntity.ok(webMapper.toStatusBreakdownResponse(rows));
    }
}
