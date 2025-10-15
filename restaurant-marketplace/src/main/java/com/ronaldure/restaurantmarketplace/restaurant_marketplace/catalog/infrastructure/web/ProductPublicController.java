// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/web/ProductPublicController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in.GetPublicProductQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in.ListPublishedProductsQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out.InventoryAvailabilityQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query.ListPublishedProductsQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.PublicProductCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.PublicProductDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.mapper.ProductWebMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.request.ListPublishedProductsRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.response.PublicProductCardResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.response.PublicProductDetailResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/public/restaurants/{restaurantId}/products")
public class ProductPublicController {

    private final GetPublicProductQuery getPublicProduct;
    private final ListPublishedProductsQuery listPublishedProducts;
    private final ProductWebMapper webMapper;
    private final InventoryAvailabilityQuery availabilityQuery;

    public ProductPublicController(GetPublicProductQuery getPublicProduct,
                                   ListPublishedProductsQuery listPublishedProducts,
                                   ProductWebMapper webMapper,
                                   InventoryAvailabilityQuery availabilityQuery) {
        this.getPublicProduct = getPublicProduct;
        this.listPublishedProducts = listPublishedProducts;
        this.webMapper = webMapper;
        this.availabilityQuery = availabilityQuery;
    }

    // Public detail
    @GetMapping("/{productId}")
    public ResponseEntity<PublicProductDetailResponse> get(
            @PathVariable("restaurantId") Long restaurantId,
            @PathVariable("productId") Long productId) {

        PublicProductDetailView view = getPublicProduct.get(restaurantId, productId);
        boolean available = availabilityQuery.isAvailable(restaurantId, productId);

        return ResponseEntity.ok(webMapper.toPublicDetailResponse(view, available));
    }

    // Public list (usa DTO para query params)
    @GetMapping
    public ResponseEntity<PageResponse<PublicProductCardResponse>> list(
            @PathVariable("restaurantId") Long restaurantId,
            @Valid @ModelAttribute ListPublishedProductsRequest query) {

        ListPublishedProductsQueryParams params = webMapper.toParams(restaurantId, query);
        PageResponse<PublicProductCardView> result = listPublishedProducts.list(params);

        // Batch availability para evitar N+1
        var ids = result.items().stream().map(PublicProductCardView::id).toList();
        Map<Long, Boolean> availabilityById = ids.isEmpty()
                ? Map.of()
                : availabilityQuery.areAvailable(restaurantId, ids); // <- nombre correcto

        List<PublicProductCardResponse> items = result.items().stream()
                .map(v -> webMapper.toPublicCardResponse(
                        v,
                        availabilityById.getOrDefault(v.id(), true) // default true = unlimited por defecto
                ))
                .toList();

        return ResponseEntity.ok(new PageResponse<>(items, result.totalElements(), result.totalPages()));
    }
}
