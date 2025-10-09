// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/persistence/adapter/AdminOrderQueryJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.AdminOrderQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.query.ListOrdersAdminQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderStatus;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.projection.OrderAdminCardProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.repository.OrderJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class AdminOrderQueryJpaAdapter implements AdminOrderQuery {

    private static final Set<String> ALLOWED_SORTS = Set.of("createdAt", "totalAmount", "status");

    private final OrderJpaRepository jpa;

    public AdminOrderQueryJpaAdapter(OrderJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderCardView> list(TenantId tenantId, ListOrdersAdminQueryParams params, PageRequest page) {
        Sort sort = buildSort(params);
        Pageable pageable = PageRequestImpl.of(page.page(), page.size(), sort);

        OrderStatus status = params.status() == null ? null : OrderStatus.valueOf(params.status());

        Page<OrderAdminCardProjection> p = jpa.searchAdminCards(
                tenantId.value(),
                status,                           // <— enum, consistente con el tipo del campo
                params.customerId(),
                params.createdFrom(),
                params.createdTo(),
                pageable
        ); 

        var content = p.map(v -> OrderCardView.of(
                v.getId(),
                v.getStatus(),
                v.getTotalAmount(),
                v.getCurrency(),
                v.getItemsCount(),
                v.getCreatedAt()
        )).getContent();

        // Consistencia con Catalog: (content, totalElements, totalPages)
        return new PageResponse<>(content, p.getTotalElements(), p.getTotalPages());
    }

    private Sort buildSort(ListOrdersAdminQueryParams params) {
        String sortBy = params.safeSortBy(ALLOWED_SORTS, "createdAt");
        String sortDir = params.safeSortDir();
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, mapSortableProperty(sortBy));
    }

    // Whitelist mapping to entity properties
    private String mapSortableProperty(String raw) {
        return switch (raw) {
            case "totalAmount" -> "totalAmount";
            case "status" -> "status";
            case "createdAt" -> "createdAt";
            default -> "createdAt";
        };
    }

    /** Wrapper to avoid clash with our shared PageRequest record name. */
    private static final class PageRequestImpl {
        static org.springframework.data.domain.PageRequest of(int page, int size, Sort sort) {
            return org.springframework.data.domain.PageRequest.of(page, size, sort);
        }
    }
}
