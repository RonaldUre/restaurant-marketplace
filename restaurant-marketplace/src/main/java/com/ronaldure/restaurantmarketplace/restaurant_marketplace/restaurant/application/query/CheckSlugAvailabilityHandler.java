package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.CheckSlugAvailabilityQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.SlugAvailabilityQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo.Slug;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class CheckSlugAvailabilityHandler implements CheckSlugAvailabilityQuery {

    private final SlugAvailabilityQuery slugAvailabilityQuery;

    // Opcional: palabras reservadas (no asignables)
    private static final Set<String> RESERVED = Set.of(
            "admin", "api", "platform", "public", "restaurants", "login", "signup"
    );

    public CheckSlugAvailabilityHandler(SlugAvailabilityQuery slugAvailabilityQuery) {
        this.slugAvailabilityQuery = slugAvailabilityQuery;
    }

    @Override
    @Transactional(readOnly = true)
    public Result check(String candidate) {
        if (candidate == null || candidate.isBlank()) {
            throw new IllegalArgumentException("slug is required");
        }

        // Normaliza/valida usando el VO (regex kebab-case, longitud, etc.)
        String normalized = Slug.of(candidate).value();

        boolean reserved = RESERVED.contains(normalized);
        boolean exists = slugAvailabilityQuery.existsBySlug(normalized);

        boolean available = !reserved && !exists;
        return new Result(candidate, normalized, available);
    }
}
