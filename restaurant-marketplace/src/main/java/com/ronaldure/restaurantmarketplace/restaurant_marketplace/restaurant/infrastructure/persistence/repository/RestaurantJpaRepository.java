// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/infrastructure/persistence/repository/RestaurantJpaRepository.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.entity.JpaRestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantJpaRepository extends JpaRepository<JpaRestaurantEntity, Long> {

    Optional<JpaRestaurantEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
