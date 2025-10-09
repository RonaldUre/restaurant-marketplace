package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.mapper;

import org.springframework.stereotype.Component;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.domain.PaymentTransaction;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.entity.JpaPaymentTransactionEntity;

// payments/infrastructure/mapper/PaymentsPersistenceMapper.java
@Component
public class PaymentsPersistenceMapper {
  public JpaPaymentTransactionEntity toNewEntity(PaymentTransaction tx) {
    var e = new JpaPaymentTransactionEntity();
    apply(e, tx);
    return e;
  }
  public void apply(JpaPaymentTransactionEntity e, PaymentTransaction tx) {
    e.setOrderId(tx.orderId().value());
    e.setTenantId(tx.tenantId().value());
    e.setAmount(tx.amount().amount());     // ideal: getters en Money
    e.setCurrency(tx.amount().currency());
    e.setMethod(tx.method());
    e.setStatus(tx.status().name());
    e.setTxId(tx.txId());
    e.setReason(tx.reason());
    // createdAt lo maneja @PrePersist
  }
}

