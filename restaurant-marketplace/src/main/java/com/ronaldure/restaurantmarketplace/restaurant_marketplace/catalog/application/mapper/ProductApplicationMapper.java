// src/main/java/.../catalog/application/mapper/ProductApplicationMapper.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.PublicProductCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.PublicProductDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductApplicationMapper {

    // Domain -> Admin detail view
    public ProductAdminDetailView toAdminDetail(Product product) {
        return new ProductAdminDetailView(
                product.id() != null ? product.id().value() : null,
                product.sku().value(),
                product.name().value(),
                product.description().value(),
                product.category().value(),
                product.price().amount(),
                product.price().currency(),
                product.published(),
                product.createdAt(),
                product.updatedAt());
    }

    // Domain -> Admin card view (for admin listings)
    public ProductAdminCardView toAdminCard(Product product) {
        return new ProductAdminCardView(
                product.id() != null ? product.id().value() : null,
                product.sku().value(),
                product.name().value(),
                product.category().value(),
                product.price().amount(),
                product.price().currency(),
                product.published(),
                product.createdAt());
    }

    // Domain -> Public card (if you ever need to map directly)
    public PublicProductCardView toPublicCard(Product product) {
        return new PublicProductCardView(
                product.id() != null ? product.id().value() : null,
                product.name().value(),
                product.category().value(),
                product.price().amount(),
                product.price().currency());
    }

    // Domain -> Public detail
    public PublicProductDetailView toPublicDetail(Product product) {
        return new PublicProductDetailView(
                product.id() != null ? product.id().value() : null,
                product.name().value(),
                product.description().value(),
                product.category().value(),
                product.price().amount(),
                product.price().currency());
    }
}
