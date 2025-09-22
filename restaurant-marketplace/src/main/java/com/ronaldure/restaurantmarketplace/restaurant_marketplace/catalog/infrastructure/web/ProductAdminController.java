// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/web/ProductAdminController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.command.CreateProductCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.command.UpdateProductCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query.ListProductsAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.mapper.ProductWebMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.ProductAdminCardResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.ProductAdminDetailResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Set;

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

    // Create
    @PostMapping
    public ResponseEntity<ProductAdminDetailResponse> create(@RequestBody CreateProductCommand body) {
        ProductAdminDetailView view = createProduct.create(body);
        return ResponseEntity.ok(webMapper.toAdminDetailResponse(view));
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<ProductAdminDetailResponse> update(@PathVariable("id") Long id,
                                                             @RequestBody UpdateBody body) {
        UpdateProductCommand cmd = new UpdateProductCommand(
                id,
                body.name(),
                body.description(),
                body.category(),
                body.priceAmount(),
                body.priceCurrency()
        );
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

    // List (admin)
    @GetMapping
    public ResponseEntity<PageResponse<ProductAdminCardResponse>> list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "category", required = false) Set<String> categories,
            @RequestParam(value = "published", required = false) Boolean published,
            @RequestParam(value = "createdFrom", required = false) Instant createdFrom,
            @RequestParam(value = "createdTo", required = false) Instant createdTo,
            @RequestParam(value = "sort", required = false) String sort,           // e.g. "createdAt,desc"
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        var params = new ListProductsAdminQueryParams(q, categories, published, createdFrom, createdTo, sort);
        var pageReq = new PageRequest(page, size);

        PageResponse<ProductAdminCardView> result = listProductsAdmin.list(params, pageReq);

        List<ProductAdminCardResponse> items = result.items().stream()
                .map(webMapper::toAdminCardResponse)
                .toList();

        return ResponseEntity.ok(new PageResponse<>(items, result.totalElements(), result.totalPages()));
    }

    /** Update body (decoupled from command to tomar el id desde el path). */
    public record UpdateBody(
            String name,
            String description,
            String category,
            java.math.BigDecimal priceAmount,
            String priceCurrency
    ) {}
}
