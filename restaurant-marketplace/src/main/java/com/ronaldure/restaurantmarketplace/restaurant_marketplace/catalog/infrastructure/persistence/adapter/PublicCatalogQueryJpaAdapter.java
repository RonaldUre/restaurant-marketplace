package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.out.PublicCatalogQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.query.ListPublishedProductsQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.PublicProductCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.projection.PublicProductCardProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.repository.PublicProductJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class PublicCatalogQueryJpaAdapter implements PublicCatalogQuery {

    private static final Set<String> ALLOWED_SORTS = Set.of("name", "priceAmount");

    private final PublicProductJpaRepository repo;

    public PublicCatalogQueryJpaAdapter(PublicProductJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PublicProductCardView> listPublished(ListPublishedProductsQueryParams params, PageRequest page) {
        Pageable pageable = buildPageable(params, page);

        Page<PublicProductCardProjection> p = repo.findPublicCards(
                params.restaurantId(),
                nullSafe(params.q()),
                nullSafe(params.category()),
                pageable
        );

        return new PageResponse<>(
                p.map(this::toView).getContent(),
                p.getTotalElements(),
                p.getTotalPages()
        );
    }

    private Pageable buildPageable(ListPublishedProductsQueryParams params, PageRequest page) {
        // Normaliza sort con helpers del record; default público: name asc
        String sortBy = params.safeSortBy(ALLOWED_SORTS, "name");
        String sortDir = params.safeSortDir();

        String property = mapSortableProperty(sortBy);
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, property);
        return PageRequestImpl.of(page.page(), page.size(), sort);
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

    // Mapea alias permitidos -> columnas reales JPA
    private String mapSortableProperty(String raw) {
        return switch (raw) {
            case "priceAmount" -> "priceAmount";
            case "name" -> "name";
            default -> "name";
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
