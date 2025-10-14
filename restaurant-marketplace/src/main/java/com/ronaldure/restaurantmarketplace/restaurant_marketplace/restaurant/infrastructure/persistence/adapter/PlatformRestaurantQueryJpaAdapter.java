package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.PlatformRestaurantQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query.ListRestaurantsPlatformQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.PlatformRestaurantCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantForSelectView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.projection.PlatformRestaurantCardProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.projection.PlatformRestaurantDetailProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.projection.PlatformRestaurantSelectProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.repository.PlatformRestaurantJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PlatformRestaurantQueryJpaAdapter implements PlatformRestaurantQuery {

    private final PlatformRestaurantJpaRepository repo;

    public PlatformRestaurantQueryJpaAdapter(PlatformRestaurantJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public PageResponse<PlatformRestaurantCardView> list(ListRestaurantsPlatformQueryParams params) {
        int page = Math.max(params.page(), 0);
        int size = Math.max(params.size(), 1);

        String sortBy = switch (params.safeSortBy()) {
            case "name" -> "name";
            case "status" -> "status";
            default -> "createdAt";
        };
        Sort.Direction dir = "asc".equalsIgnoreCase(params.safeSortDir()) ? Sort.Direction.ASC : Sort.Direction.DESC;

        var pageable = org.springframework.data.domain.PageRequest.of(page, size, Sort.by(dir, sortBy));

        List<String> statuses = params.statuses();
        boolean includeAllStatuses = (statuses == null || statuses.isEmpty());

        Page<PlatformRestaurantCardProjection> p = repo.listPlatform(
                pageable,
                includeAllStatuses,
                includeAllStatuses ? List.of("OPEN", "CLOSED", "SUSPENDED") : statuses,
                params.city(),
                safeTrimOrNull(params.q()),
                params.createdFrom(),
                params.createdTo()
        );

        var items = p.getContent().stream().map(this::toCardView).toList();
        return new PageResponse<>(items, p.getTotalElements(), p.getTotalPages());
    }

    @Override
    public Optional<RestaurantView> getById(Long id) {
        return repo.getDetailById(id).map(this::toRestaurantView);
    }

    @Override
    public List<RestaurantForSelectView> listAllForSelect() { // ← NUEVO
        List<PlatformRestaurantSelectProjection> rows = repo.findAllForSelect();
        return rows.stream()
                .map(r -> new RestaurantForSelectView(r.getId(), r.getName()))
                .toList();
    }

    private String safeTrimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }



    private PlatformRestaurantCardView toCardView(PlatformRestaurantCardProjection prj) {
        return new PlatformRestaurantCardView(
                prj.getId(),
                prj.getName(),
                prj.getSlug(),
                prj.getStatus(),
                prj.getCity(),
                prj.getCreatedAt()
        );
    }

    private RestaurantView toRestaurantView(PlatformRestaurantDetailProjection prj) {
        RestaurantView.AddressView address =
                (prj.getAddressLine1() == null &&
                 prj.getAddressLine2() == null &&
                 prj.getCity() == null &&
                 prj.getCountry() == null &&
                 prj.getPostalCode() == null)
                ? null
                : new RestaurantView.AddressView(
                        prj.getAddressLine1(),
                        prj.getAddressLine2(),
                        prj.getCity(),
                        prj.getCountry(),
                        prj.getPostalCode()
                );

        return new RestaurantView(
                prj.getId(),
                prj.getName(),
                prj.getSlug(),
                prj.getStatus(),
                prj.getEmail(),
                prj.getPhone(),
                address,
                prj.getOpeningHoursJson()
        );
    }
}
