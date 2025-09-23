// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/web/ProductPublicController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in.GetPublicProductQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in.ListPublishedProductsQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query.ListPublishedProductsQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.PublicProductCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.PublicProductDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.mapper.ProductWebMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.PublicProductCardResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.PublicProductDetailResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.ListPublishedProductsRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/public/restaurants/{restaurantId}/products")
public class ProductPublicController {

    private final GetPublicProductQuery getPublicProduct;
    private final ListPublishedProductsQuery listPublishedProducts;
    private final ProductWebMapper webMapper;

    public ProductPublicController(GetPublicProductQuery getPublicProduct,
            ListPublishedProductsQuery listPublishedProducts,
            ProductWebMapper webMapper) {
        this.getPublicProduct = getPublicProduct;
        this.listPublishedProducts = listPublishedProducts;
        this.webMapper = webMapper;
    }

    // Public detail
    @GetMapping("/{productId}")
    public ResponseEntity<PublicProductDetailResponse> get(@PathVariable("restaurantId") Long restaurantId,
            @PathVariable("productId") Long productId) {
        PublicProductDetailView view = getPublicProduct.get(restaurantId, productId);
        return ResponseEntity.ok(webMapper.toPublicDetailResponse(view));
    }

    // Public list (usa DTO para query params)
    @GetMapping
    public ResponseEntity<PageResponse<PublicProductCardResponse>> list(
            @PathVariable("restaurantId") Long restaurantId,
            @Valid @ModelAttribute ListPublishedProductsRequest query,
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(200) int size) {
        ListPublishedProductsQueryParams params = webMapper.toParams(restaurantId, query);
        var pageReq = new PageRequest(page, size);

        PageResponse<PublicProductCardView> result = listPublishedProducts.list(params, pageReq);

        List<PublicProductCardResponse> items = result.items().stream()
                .map(webMapper::toPublicCardResponse)
                .toList();

        return ResponseEntity.ok(new PageResponse<>(items, result.totalElements(), result.totalPages()));
    }
}
