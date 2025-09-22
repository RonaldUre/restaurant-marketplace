// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/persistence/adapter/PublicCatalogQueryJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out.PublicCatalogQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query.ListPublishedProductsQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.PublicProductCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.projection.PublicProductCardProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.repository.PublicProductJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo.Status;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PublicCatalogQueryJpaAdapter implements PublicCatalogQuery {

    private final PublicProductJpaRepository repo;

    public PublicCatalogQueryJpaAdapter(PublicProductJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PublicProductCardView> listPublished(ListPublishedProductsQueryParams params, PageRequest page) {
        Pageable pageable = buildPageable(params.sort(), page);

        Page<PublicProductCardProjection> p = repo.findPublicCards(
                params.restaurantId(),
                nullSafe(params.q()),
                nullSafe(params.category()),
                Status.OPEN,
                pageable
        );

        return new PageResponse<>(
                p.map(this::toView).getContent(),
                p.getTotalElements(),
                p.getTotalPages()
        );
    }

    private PublicProductCardView toView(PublicProductCardProjection p) {
        return new PublicProductCardView(
                p.getId(),
                p.getName(),
                p.getCategory(),
                p.getPriceAmount(),
                p.getPriceCurrency()
        );
    }

    private Pageable buildPageable(String sortSpec, PageRequest page) {
        // sort solo desde params; default name asc
        String spec = (sortSpec != null && !sortSpec.isBlank()) ? sortSpec : "name,asc";
        Sort sort = parseSort(spec);
        return PageRequestImpl.of(page.page(), page.size(), sort);
    }

    private Sort parseSort(String spec) {
        String[] parts = spec.split(",", 2);
        String prop = mapSortableProperty(parts[0].trim());
        Sort.Direction dir = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(dir, prop);
    }

    private String mapSortableProperty(String raw) {
        return switch (raw) {
            case "name" -> "name";
            case "priceAmount" -> "priceAmount";
            default -> "name";
        };
    }

    private String nullSafe(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private static final class PageRequestImpl {
        static org.springframework.data.domain.PageRequest of(int page, int size, Sort sort) {
            return org.springframework.data.domain.PageRequest.of(page, size, sort);
        }
    }
}
