// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/persistence/mapper/ProductPersistenceMapper.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.Product;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.entity.JpaProductEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceMapper {

    // Domain -> JPA
    public JpaProductEntity toEntity(Product product) {
        return new JpaProductEntity(
                product.id() != null ? product.id().value() : null,
                product.tenantId().value(),
                product.sku().value(),
                product.name().value(),
                product.description() != null ? product.description().value() : null,
                product.price().amount(),
                product.price().currency(),
                product.category().value(),
                product.published(),
                product.createdAt(),
                product.updatedAt(),
                product.deletedAt()
        );
    }

    // JPA -> Domain
    public Product toDomain(JpaProductEntity entity) {
        return Product.rehydrate(
                entity.getId() != null ? ProductId.of(entity.getId()) : null,
                TenantId.of(entity.getTenantId()),
                Sku.of(entity.getSku()),
                ProductName.of(entity.getName()),
                entity.getDescription() == null ? ProductDescription.empty() : ProductDescription.of(entity.getDescription()),
                Category.of(entity.getCategory()),
                Money.of(entity.getPriceAmount(), entity.getPriceCurrency()),
                entity.isPublished(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
