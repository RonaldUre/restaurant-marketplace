// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/mapper/ProductWebMapper.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.command.CreateProductCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.command.UpdateProductCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query.ListProductsAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query.ListPublishedProductsQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.web.dto.*;
import org.springframework.stereotype.Component;

@Component
public class ProductWebMapper {

    // Admin
    public ProductAdminDetailResponse toAdminDetailResponse(ProductAdminDetailView view) {
        return new ProductAdminDetailResponse(
                view.id(),
                view.sku(),
                view.name(),
                view.description(),
                view.category(),
                view.priceAmount(),
                view.priceCurrency(),
                view.published(),
                view.createdAt(),
                view.updatedAt());
    }

    public ProductAdminCardResponse toAdminCardResponse(ProductAdminCardView view) {
        return new ProductAdminCardResponse(
                view.id(),
                view.sku(),
                view.name(),
                view.category(),
                view.priceAmount(),
                view.priceCurrency(),
                view.published(),
                view.createdAt());
    }

    // Public
    public PublicProductDetailResponse toPublicDetailResponse(PublicProductDetailView view) {
        return new PublicProductDetailResponse(
                view.id(),
                view.name(),
                view.description(),
                view.category(),
                view.priceAmount(),
                view.priceCurrency());
    }

    public PublicProductCardResponse toPublicCardResponse(PublicProductCardView view) {
        return new PublicProductCardResponse(
                view.id(),
                view.name(),
                view.category(),
                view.priceAmount(),
                view.priceCurrency());
    }

    // ===== nuevo: Web -> Commands =====
    public CreateProductCommand toCommand(CreateProductRequest req) {
        // description: si viene null o blank, pasa tal cual; ProductFactory lo
        // convierte a empty()
        return new CreateProductCommand(
                req.sku(),
                req.name(),
                req.description(),
                req.category(),
                req.priceAmount(),
                req.priceCurrency());
    }

    public UpdateProductCommand toCommand(Long id, UpdateProductRequest req) {
        return new UpdateProductCommand(
                id,
                req.name(),
                req.description(),
                req.category(),
                req.priceAmount(),
                req.priceCurrency());
    }

    public ListPublishedProductsQueryParams toParams(Long restaurantId, ListPublishedProductsRequest req) {
        return new ListPublishedProductsQueryParams(
                restaurantId,
                nullIfBlank(req.q()),
                nullIfBlank(req.category()),
                nullIfBlank(req.sort()));
    }

    public ListProductsAdminQueryParams toParams(ListProductsAdminRequest req) {
        return new ListProductsAdminQueryParams(
                nullIfBlank(req.q()),
                (req.categories() == null || req.categories().isEmpty()) ? null : req.categories(),
                req.published(),
                req.createdFrom(),
                req.createdTo(),
                nullIfBlank(req.sort()));
    }

    private String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

}
