// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/inventory/infrastructure/persistence/adapter/InventoryAdminQueryJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.ports.out.InventoryAdminQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.query.ListInventoryAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.view.InventoryAdminItemView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.projection.InventoryAdminItemProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.infrastructure.persistence.repository.InventoryAdminJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/** Read-model admin (paginado + filtros + sort whitelist). */
@Component
public class InventoryAdminQueryJpaAdapter implements InventoryAdminQuery {

    private static final Set<String> ALLOWED_SORTS = Set.of(
            "name", "sku", "category", "available", "reserved", "updatedAt");

    private final InventoryAdminJpaRepository repo;

    public InventoryAdminQueryJpaAdapter(InventoryAdminJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InventoryAdminItemView> list(TenantId tenantId,
            ListInventoryAdminQueryParams params,
            PageRequest page) {
        Pageable pageable = buildPageable(params, page);
        String q = nullIfBlank(params.sku()); // o renómbralo a params.q()

        Page<InventoryAdminItemProjection> p = repo.search(
                tenantId.value(),
                q,
                nullIfBlank(params.category()),
                params.productId(),
                pageable);

        return new PageResponse<>(
                p.map(this::toView).getContent(),
                p.getTotalElements(),
                p.getTotalPages());
    }

    private InventoryAdminItemView toView(InventoryAdminItemProjection prj) {
        return new InventoryAdminItemView(
                prj.getProductId(),
                prj.getSku(),
                prj.getName(),
                prj.getCategory(),
                prj.getAvailable(),
                prj.getReserved(),
                prj.getAvailable() == null,
                prj.getCreatedAt(),
                prj.getUpdatedAt());
    }

    private Pageable buildPageable(ListInventoryAdminQueryParams params, PageRequest page) {
        String sortBy = params.safeSortBy(ALLOWED_SORTS, "updatedAt");
        String sortDir = params.safeSortDir();

        String property = switch (sortBy) {
            case "name" -> "p.name";
            case "sku" -> "p.sku";
            case "category" -> "p.category";
            case "available" -> "i.available";
            case "reserved" -> "i.reserved";
            case "updatedAt" -> "i.updatedAt";
            default -> "i.updatedAt";
        };

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        // Spring Data no entiende alias de JOIN en Sort; usamos el nombre simple
        // esperado por JPA.
        String jpaProperty = mapToJpaProperty(property);
        Sort sort = Sort.by(direction, jpaProperty);
        return PageRequestImpl.of(page.page(), page.size(), sort);
    }

    private String mapToJpaProperty(String resolved) {
        // Proyección usa select ... as <names>. Para ordenar, debe existir propiedad en
        // la entidad raíz del from (i)
        // y campos del join (p) deben mapearse a alias válidos del query generado por
        // Spring (usualmente nombre simple).
        // Mapeamos a nombres simples usados en la JPQL del repositorio.
        return switch (resolved) {
            case "p.name" -> "name";
            case "p.sku" -> "sku";
            case "p.category" -> "category";
            case "i.available" -> "available";
            case "i.reserved" -> "reserved";
            case "i.updatedAt" -> "updatedAt";
            default -> "updatedAt";
        };
    }

    private String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    /** Wrapper para no chocar con tu record PageRequest. */
    private static final class PageRequestImpl {
        static org.springframework.data.domain.PageRequest of(int page, int size, Sort sort) {
            return org.springframework.data.domain.PageRequest.of(page, size, sort);
        }
    }
}
