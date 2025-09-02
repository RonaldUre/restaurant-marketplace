package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.RestaurantRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.Restaurant;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.entity.JpaRestaurantEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.repository.RestaurantJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RestaurantRepositoryJpaAdapter implements RestaurantRepository {

    private final RestaurantJpaRepository jpaRepository;

    public RestaurantRepositoryJpaAdapter(RestaurantJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    // -------- Queries --------
    @Override
    public Optional<Restaurant> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Restaurant> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(this::toDomain);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }

    // -------- Save --------
    @Override
    public Restaurant save(Restaurant restaurant) {
        JpaRestaurantEntity entity = toEntity(restaurant);
        JpaRestaurantEntity saved = jpaRepository.save(entity);
        return toDomain(saved); // devolver agregado con ID asignado
    }

    // -------- Mapping: JPA -> Domain --------
    private Restaurant toDomain(JpaRestaurantEntity e) {
        return Restaurant.rehydrate(
                e.getId() != null ? RestaurantId.of(e.getId()) : null,
                Name.of(e.getName()),
                Slug.of(e.getSlug()),
                e.getEmail() != null ? Email.of(e.getEmail()) : null,
                e.getPhone() != null ? Phone.of(e.getPhone()) : null,
                Address.of(e.getAddressLine1(), e.getAddressLine2(), e.getCity(), e.getCountry(), e.getPostalCode()),
                e.getOpeningHoursJson() != null ? OpeningHours.of(e.getOpeningHoursJson()) : null,
                Status.valueOf(e.getStatus())
        );
    }

    // -------- Mapping: Domain -> JPA --------
    private JpaRestaurantEntity toEntity(Restaurant r) {
        return new JpaRestaurantEntity(
                r.id() != null ? r.id().value() : null,
                r.name() != null ? r.name().value() : null,
                r.slug() != null ? r.slug().value() : null,
                r.status() != null ? r.status().name() : null,
                r.email() != null ? r.email().value() : null,
                r.phone() != null ? r.phone().value() : null,
                r.address() != null ? r.address().line1() : null,
                r.address() != null ? r.address().line2() : null,
                r.address() != null ? r.address().city() : null,
                r.address() != null ? r.address().country() : null,
                r.address() != null ? r.address().postalCode() : null,
                r.openingHours() != null ? r.openingHours().json() : null
        );
    }
}
