// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/mapper/ProductWebMapper.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.mapper;

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
                view.updatedAt()
        );
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
                view.createdAt()
        );
    }

    // Public
    public PublicProductDetailResponse toPublicDetailResponse(PublicProductDetailView view) {
        return new PublicProductDetailResponse(
                view.id(),
                view.name(),
                view.description(),
                view.category(),
                view.priceAmount(),
                view.priceCurrency()
        );
    }

    public PublicProductCardResponse toPublicCardResponse(PublicProductCardView view) {
        return new PublicProductCardResponse(
                view.id(),
                view.name(),
                view.category(),
                view.priceAmount(),
                view.priceCurrency()
        );
    }
}
