package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.PublicOrderQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.query.ListOrdersPublicQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderStatus;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.projection.OrderAdminCardProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.repository.OrderJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PublicOrderQueryJpaAdapter implements PublicOrderQuery {

    private final OrderJpaRepository jpa;

    public PublicOrderQueryJpaAdapter(OrderJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderCardView> list(UserId ownerId, ListOrdersPublicQueryParams params, PageRequest page) {
        Sort sort = Sort.by("asc".equalsIgnoreCase(params.safeSortDir()) ? Sort.Direction.ASC : Sort.Direction.DESC,
                mapSortable(params.safeSortBy()));
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page.page(), page.size(), sort);

        OrderStatus status = params.status() == null ? null : OrderStatus.valueOf(params.status());

        Page<OrderAdminCardProjection> p = jpa.searchPublicCards(
                Long.parseLong(ownerId.value()),
                status,
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

        return new PageResponse<>(content, p.getTotalElements(), p.getTotalPages());
    }

    private String mapSortable(String raw) {
        return switch (raw) {
            case "totalAmount" -> "totalAmount";
            case "status" -> "status";
            case "createdAt" -> "createdAt";
            default -> "createdAt";
        };
    }
}
