package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.persistence.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.persistence.entity.JpaRefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<JpaRefreshTokenEntity, Long> {
    Optional<JpaRefreshTokenEntity> findByJti(String jti);
    List<JpaRefreshTokenEntity> findAllByUserIdAndRevokedFalse(Long userId);
}
