// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/persistence/adapter/PublicProductDetailQueryJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out.PublicProductDetailQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.PublicProductDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.projection.PublicProductDetailProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.repository.PublicProductJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class PublicProductDetailQueryJpaAdapter implements PublicProductDetailQuery {

    private final PublicProductJpaRepository repo;

    public PublicProductDetailQueryJpaAdapter(PublicProductJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PublicProductDetailView> findByRestaurantAndId(Long restaurantId, Long productId) {
        return repo.findPublicDetail(restaurantId, productId)
                   .map(this::toView);
    }

    private PublicProductDetailView toView(PublicProductDetailProjection p) {
        return new PublicProductDetailView(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getCategory(),
                p.getPriceAmount(),
                p.getPriceCurrency()
        );
    }
}
