package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.SlugAvailabilityQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.repository.PublicRestaurantJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class SlugQueryJpaAdapter implements SlugAvailabilityQuery {

    private final PublicRestaurantJpaRepository repo;

    public SlugQueryJpaAdapter(PublicRestaurantJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean existsBySlug(String normalizedSlug) {
        return repo.existsBySlug(normalizedSlug);
    }
}
