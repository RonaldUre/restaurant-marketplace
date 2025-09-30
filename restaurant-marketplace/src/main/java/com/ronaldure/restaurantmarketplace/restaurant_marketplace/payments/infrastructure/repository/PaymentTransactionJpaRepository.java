package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.entity.JpaPaymentTransactionEntity;

public interface PaymentTransactionJpaRepository {

    void save(JpaPaymentTransactionEntity entity);
}
