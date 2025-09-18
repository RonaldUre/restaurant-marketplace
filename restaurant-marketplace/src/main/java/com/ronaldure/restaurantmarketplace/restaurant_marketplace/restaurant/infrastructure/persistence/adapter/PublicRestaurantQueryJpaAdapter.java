// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/infrastructure/persistence/adapter/PublicRestaurantQueryJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.PublicRestaurantQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.projection.PublicRestaurantCardProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.projection.PublicRestaurantDetailProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.repository.PublicRestaurantJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA adapter for PublicRestaurantQuery.
 * - Uses read-optimized queries (projections) and maps to application-level views.
 * - No aggregate rehydration here (CQRS-light).
 */
@Component
public class PublicRestaurantQueryJpaAdapter implements PublicRestaurantQuery {

    private final PublicRestaurantJpaRepository repo;

    public PublicRestaurantQueryJpaAdapter(PublicRestaurantJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public PageResponse<RestaurantCardView> listPublic(PageRequest page, String cityFilter) {
        // Avoid name clash with your own PageRequest record by using fully qualified name or an alias.
        Page<PublicRestaurantCardProjection> p = repo.listOpen(
                org.springframework.data.domain.PageRequest.of(Math.max(page.page(), 0), Math.max(page.size(), 1)),
                cityFilter
        );

        var items = p.getContent().stream()
                .map(this::toCardView)
                .collect(Collectors.toList());

        return new PageResponse<>(
                items,
                p.getTotalElements(),
                p.getTotalPages()
        );
    }

    @Override
    public Optional<RestaurantView> getBySlug(String slug) {
        return repo.getDetailBySlug(slug).map(this::toRestaurantView);
    }

    @Override
    public Optional<RestaurantView> getById(Long id) {
        return repo.getDetailById(id).map(this::toRestaurantView);
    }

    // -------- Mapping helpers --------

    private RestaurantCardView toCardView(PublicRestaurantCardProjection prj) {
        return new RestaurantCardView(
                prj.getId(),
                prj.getName(),
                prj.getSlug(),
                prj.getStatus(),
                prj.getCity()
        );
    }

    private RestaurantView toRestaurantView(PublicRestaurantDetailProjection prj) {
        RestaurantView.AddressView address = (prj.getAddressLine1() == null &&
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
