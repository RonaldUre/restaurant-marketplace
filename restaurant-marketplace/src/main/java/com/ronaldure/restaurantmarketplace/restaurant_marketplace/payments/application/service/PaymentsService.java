package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.domain.PaymentTransaction;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.application.view.PaymentTransactionView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.repository.PaymentTransactionJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.mapper.PaymentsPersistenceMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PaymentsService {
  private final PaymentTransactionJpaRepository repo;
  private final PaymentsPersistenceMapper mapper;

  public PaymentsService(PaymentTransactionJpaRepository repo,
                         PaymentsPersistenceMapper mapper) {
    this.repo = repo;
    this.mapper = mapper;
  }

  @Transactional
  public PaymentTransactionView recordInitiated(OrderId orderId, TenantId tenantId, Money amount, String method) {
    return upsert(PaymentTransaction.initiated(orderId, tenantId, amount, method));
  }

  @Transactional
  public PaymentTransactionView recordApproved(OrderId orderId, TenantId tenantId, Money amount, String method, String txId) {
    return upsert(PaymentTransaction.approved(orderId, tenantId, amount, method, txId));
  }

  @Transactional
  public PaymentTransactionView recordDeclined(OrderId orderId, TenantId tenantId, Money amount, String method, String reason) {
    return upsert(PaymentTransaction.declined(orderId, tenantId, amount, method, reason));
  }

  private PaymentTransactionView upsert(PaymentTransaction tx) {
    var existing = repo.findByOrderId(tx.orderId().value()).orElse(null);
    if (existing == null) {
      repo.save(mapper.toNewEntity(tx));
    } else {
      // Precedencia: APPROVED > DECLINED > INITIATED
      var current = existing.getStatus();
      if ("APPROVED".equals(current)) {
        // No pisar un aprobado
      } else if ("DECLINED".equals(current) && tx.status() == PaymentTransaction.Status.INITIATED) {
        // No degradar
      } else {
        mapper.apply(existing, tx);
        repo.save(existing);
      }
    }
    return PaymentTransactionView.of(
      tx.orderId().value(), tx.tenantId().value(),
      tx.amount().amount(), tx.amount().currency(),
      tx.method(), tx.status().name(), tx.txId(), tx.reason(), tx.createdAt()
    );
  }
}
