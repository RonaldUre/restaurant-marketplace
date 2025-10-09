// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/infrastructure/persistence/adapter/AdminProductQueryJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out.AdminProductQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query.ListProductsAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.projection.ProductAdminCardProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.repository.AdminProductJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;

import java.util.Set;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminProductQueryJpaAdapter implements AdminProductQuery {

    private static final Set<String> ALLOWED_SORTS = Set.of(
        "createdAt", "name", "sku", "category", "priceAmount", "published"
    );

    private final AdminProductJpaRepository repo;

    public AdminProductQueryJpaAdapter(AdminProductJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductAdminCardView> list(
            TenantId tenantId,
            ListProductsAdminQueryParams params,
            PageRequest page) {

        Pageable pageable = buildPageable(params, page); // <— usa params, no params.sort()

        String category = (params.categories() != null && !params.categories().isEmpty())
                ? params.categories().iterator().next()
                : null;

        Page<ProductAdminCardProjection> p = repo.search(
                tenantId.value(),
                nullSafe(params.q()),
                category,
                params.published(),
                pageable
        );

        return new PageResponse<>(
                p.map(this::toView).getContent(),
                p.getTotalElements(),
                p.getTotalPages()
        );
    }

    // NUEVO: construye el Pageable desde sortBy/sortDir normalizados
    private Pageable buildPageable(ListProductsAdminQueryParams params, PageRequest page) {
        // normaliza con helpers del record
        String sortBy = params.safeSortBy(ALLOWED_SORTS, "createdAt");
        String sortDir = params.safeSortDir();

        String property = mapSortableProperty(sortBy);
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, property);
        return PageRequestImpl.of(page.page(), page.size(), sort);
    }

    private ProductAdminCardView toView(ProductAdminCardProjection p) {
        return new ProductAdminCardView(
                p.getId(),
                p.getSku(),
                p.getName(),
                p.getCategory(),
                p.getPriceAmount(),
                p.getPriceCurrency(),
                p.isPublished(),
                p.getCreatedAt()
        );
    }

    // Mapea los alias permitidos a campos de JPA (whitelist)
    private String mapSortableProperty(String raw) {
        return switch (raw) {
            case "name" -> "name";
            case "sku" -> "sku";
            case "category" -> "category";
            case "priceAmount" -> "priceAmount";
            case "published" -> "published";
            case "createdAt" -> "createdAt";
            default -> "createdAt";
        };
    }

    private String nullSafe(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    /** Wrapper para no chocar con tu record PageRequest. */
    private static final class PageRequestImpl {
        static org.springframework.data.domain.PageRequest of(int page, int size, Sort sort) {
            return org.springframework.data.domain.PageRequest.of(page, size, sort);
        }
    }
}
