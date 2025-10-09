package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.persistence.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.persistence.entity.JpaRefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<JpaRefreshTokenEntity, Long> {
    Optional<JpaRefreshTokenEntity> findByJti(String jti);

    List<JpaRefreshTokenEntity> findAllByUserIdAndRevokedFalse(Long userId);
    List<JpaRefreshTokenEntity> findAllByUserIdAndSubjectTypeAndRevokedFalse(Long userId, String subjectType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from JpaRefreshTokenEntity r where r.jti = :jti")
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "4000")) // opcional
    Optional<JpaRefreshTokenEntity> findByJtiWithPessimisticLock(@Param("jti") String jti);
}
