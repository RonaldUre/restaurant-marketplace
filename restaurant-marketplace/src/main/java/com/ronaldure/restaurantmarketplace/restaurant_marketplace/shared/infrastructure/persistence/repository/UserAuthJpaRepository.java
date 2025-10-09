package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.persistence.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.persistence.entity.JpaUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthJpaRepository extends JpaRepository<JpaUserEntity, Long> {
    Optional<JpaUserEntity> findByEmail(String email);
}
