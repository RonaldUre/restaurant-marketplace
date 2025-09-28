// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/web/ProductAdminController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.command.CreateProductCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.command.UpdateProductCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query.ListProductsAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.mapper.ProductWebMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.request.CreateProductRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.request.ListProductsAdminRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.request.UpdateProductRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.response.ProductAdminCardResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.response.ProductAdminDetailResponse;
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

    // Create → 201 Created + Location
    @PostMapping
    public ResponseEntity<ProductAdminDetailResponse> create(@RequestBody @Valid CreateProductRequest body) {
        CreateProductCommand cmd = webMapper.toCommand(body);
        ProductAdminDetailView view = createProduct.create(cmd);
        ProductAdminDetailResponse resp = webMapper.toAdminDetailResponse(view);

        java.net.URI location = java.net.URI.create("/admin/products/" + resp.id());
        return ResponseEntity.created(location).body(resp);
    }

    // Update → 200 OK
    @PutMapping("/{id}")
    public ResponseEntity<ProductAdminDetailResponse> update(@PathVariable("id") Long id,
                                                             @RequestBody @Valid UpdateProductRequest body) {
        UpdateProductCommand cmd = webMapper.toCommand(id, body);
        ProductAdminDetailView view = updateProduct.update(cmd);
        return ResponseEntity.ok(webMapper.toAdminDetailResponse(view));
    }

    // Publish → 200 OK
    @PostMapping("/{id}/publish")
    public ResponseEntity<ProductAdminDetailResponse> publish(@PathVariable("id") Long id) {
        ProductAdminDetailView view = publishProduct.publish(id);
        return ResponseEntity.ok(webMapper.toAdminDetailResponse(view));
    }

    // Unpublish → 200 OK
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<ProductAdminDetailResponse> unpublish(@PathVariable("id") Long id) {
        ProductAdminDetailView view = unpublishProduct.unpublish(id);
        return ResponseEntity.ok(webMapper.toAdminDetailResponse(view));
    }

    // Detail → 200 OK
    @GetMapping("/{id}")
    public ResponseEntity<ProductAdminDetailResponse> get(@PathVariable("id") Long id) {
        ProductAdminDetailView view = getProductAdmin.get(id);
        return ResponseEntity.ok(webMapper.toAdminDetailResponse(view));
    }

    // List (admin) → 200 OK
    @GetMapping
    public ResponseEntity<PageResponse<ProductAdminCardResponse>> list(
            @Valid @ModelAttribute ListProductsAdminRequest query) {

        ListProductsAdminQueryParams params = webMapper.toParams(query);
        PageResponse<ProductAdminCardView> result = listProductsAdmin.list(params);

        List<ProductAdminCardResponse> items = result.items().stream()
                .map(webMapper::toAdminCardResponse)
                .toList();

        return ResponseEntity.ok(new PageResponse<>(items, result.totalElements(), result.totalPages()));
    }
}