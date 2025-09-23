// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/web/ProductAdminController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query.ListProductsAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.mapper.ProductWebMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/products")
public class ProductAdminController {

    private final CreateProductUseCase createProduct;
    private final UpdateProductUseCase updateProduct;
    private final PublishProductUseCase publishProduct;
    private final UnpublishProductUseCase unpublishProduct;
    private final GetProductAdminQuery getProductAdmin;
    private final ListProductsAdminQuery listProductsAdmin;
    private final ProductWebMapper webMapper;

    public ProductAdminController(CreateProductUseCase createProduct,
                                  UpdateProductUseCase updateProduct,
                                  PublishProductUseCase publishProduct,
                                  UnpublishProductUseCase unpublishProduct,
                                  GetProductAdminQuery getProductAdmin,
                                  ListProductsAdminQuery listProductsAdmin,
                                  ProductWebMapper webMapper) {
        this.createProduct = createProduct;
        this.updateProduct = updateProduct;
        this.publishProduct = publishProduct;
        this.unpublishProduct = unpublishProduct;
        this.getProductAdmin = getProductAdmin;
        this.listProductsAdmin = listProductsAdmin;
        this.webMapper = webMapper;
    }

    // Create (usa DTO de request + validación)
    @PostMapping
    public ResponseEntity<ProductAdminDetailResponse> create(@RequestBody @Valid CreateProductRequest body) {
        var cmd = webMapper.toCommand(body);
        ProductAdminDetailView view = createProduct.create(cmd);
        return ResponseEntity.ok(webMapper.toAdminDetailResponse(view));
    }

    // Update (id por path + DTO de request)
    @PutMapping("/{id}")
    public ResponseEntity<ProductAdminDetailResponse> update(@PathVariable("id") Long id,
                                                             @RequestBody @Valid UpdateProductRequest body) {
        var cmd = webMapper.toCommand(id, body);
        ProductAdminDetailView view = updateProduct.update(cmd);
        return ResponseEntity.ok(webMapper.toAdminDetailResponse(view));
    }

    // Publish
    @PostMapping("/{id}/publish")
    public ResponseEntity<Void> publish(@PathVariable("id") Long id) {
        publishProduct.publish(id);
        return ResponseEntity.noContent().build();
    }

    // Unpublish
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<Void> unpublish(@PathVariable("id") Long id) {
        unpublishProduct.unpublish(id);
        return ResponseEntity.noContent().build();
    }

    // Detail
    @GetMapping("/{id}")
    public ResponseEntity<ProductAdminDetailResponse> get(@PathVariable("id") Long id) {
        ProductAdminDetailView view = getProductAdmin.get(id);
        return ResponseEntity.ok(webMapper.toAdminDetailResponse(view));
    }

    // List (admin) — DTO para query params + paginación aparte
    @GetMapping
    public ResponseEntity<PageResponse<ProductAdminCardResponse>> list(
            @Valid @ModelAttribute ListProductsAdminRequest query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        ListProductsAdminQueryParams params = webMapper.toParams(query);
        var pageReq = new PageRequest(page, size);

        PageResponse<ProductAdminCardView> result = listProductsAdmin.list(params, pageReq);

        List<ProductAdminCardResponse> items = result.items().stream()
                .map(webMapper::toAdminCardResponse)
                .toList();

        return ResponseEntity.ok(new PageResponse<>(items, result.totalElements(), result.totalPages()));
    }
}
