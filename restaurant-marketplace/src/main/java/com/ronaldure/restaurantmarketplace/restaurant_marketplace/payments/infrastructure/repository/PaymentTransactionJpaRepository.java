package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.entity.JpaPaymentTransactionEntity;

public interface PaymentTransactionJpaRepository
    extends JpaRepository<JpaPaymentTransactionEntity, Long> {
  Optional<JpaPaymentTransactionEntity> findByOrderId(Long orderId);
}
