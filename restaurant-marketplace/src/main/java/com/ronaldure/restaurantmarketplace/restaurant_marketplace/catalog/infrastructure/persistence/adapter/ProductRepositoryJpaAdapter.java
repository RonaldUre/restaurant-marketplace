// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/persistence/adapter/ProductRepositoryJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out.ProductRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.Product;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.ProductId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.Sku;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.entity.JpaProductEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.mapper.ProductPersistenceMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.repository.ProductJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class ProductRepositoryJpaAdapter implements ProductRepository {

    private final ProductJpaRepository jpa;
    private final ProductPersistenceMapper mapper;

    public ProductRepositoryJpaAdapter(ProductJpaRepository jpa,
                                       ProductPersistenceMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Product save(Product product) {
        // Domain -> JPA
        JpaProductEntity entity = mapper.toEntity(product);

        // Persist
        JpaProductEntity saved = jpa.save(entity);

        // JPA -> Domain (rehydrated with generated id/timestamps)
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(ProductId id, TenantId tenantId) {
        return jpa.findByIdAndTenantIdAndDeletedAtIsNull(id.value(), tenantId.value())
                  .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTenantAndSku(TenantId tenantId, Sku sku) {
        // DB constraint enforces uniqueness; we check pre-insert for friendly error
        return jpa.existsByTenantIdAndSkuIgnoreCaseAndDeletedAtIsNull(tenantId.value(), sku.value());
    }
}
