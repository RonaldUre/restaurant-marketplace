package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.domain.PaymentTransaction;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.application.view.PaymentTransactionView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.repository.PaymentTransactionJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.entity.JpaPaymentTransactionEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.mapper.PaymentsPersistenceMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;

import java.util.Optional;

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
  public PaymentTransactionView recordApproved(OrderId orderId, TenantId tenantId, Money amount, String method,
      String txId) {
    return upsert(PaymentTransaction.approved(orderId, tenantId, amount, method, txId));
  }

  @Transactional
  public PaymentTransactionView recordDeclined(OrderId orderId, TenantId tenantId, Money amount, String method,
      String reason) {
    return upsert(PaymentTransaction.declined(orderId, tenantId, amount, method, reason));
  }

  //solo para el adapter fake, se puede borrar luego
  @Transactional(readOnly = true)
  public Optional<PaymentTransactionView> findByOrderId(Long orderId) {
    return repo.findByOrderId(orderId).map(this::mapEntityToView);
  }

  private PaymentTransactionView upsert(PaymentTransaction tx) {
    var existingOpt = repo.findByOrderId(tx.orderId().value());
    JpaPaymentTransactionEntity persisted;

    if (existingOpt.isEmpty()) {
      persisted = repo.save(mapper.toNewEntity(tx)); // @PrePersist setea createdAt real
    } else {
      var existing = existingOpt.get();
      var current = existing.getStatus();

      if ("APPROVED".equals(current)) {
        // No pisar un aprobado
        persisted = existing;
      } else if ("DECLINED".equals(current) && tx.status() == PaymentTransaction.Status.INITIATED) {
        // No degradar
        persisted = existing;
      } else {
        mapper.apply(existing, tx);
        persisted = repo.save(existing);
      }
    }

    return PaymentTransactionView.of(
        persisted.getOrderId(),
        persisted.getTenantId(),
        persisted.getAmount(),
        persisted.getCurrency(),
        persisted.getMethod(),
        persisted.getStatus(),
        persisted.getTxId(),
        persisted.getReason(),
        persisted.getCreatedAt() // ← ahora viene de DB
    );
  }

      // 👇 HELPER SOLO PARA EL FAKE ADAPTER SE PUEDE BORRAR👇
    private PaymentTransactionView mapEntityToView(JpaPaymentTransactionEntity entity) {
        return PaymentTransactionView.of(
            entity.getOrderId(),
            entity.getTenantId(),
            entity.getAmount(),
            entity.getCurrency(),
            entity.getMethod(),
            entity.getStatus(),
            entity.getTxId(),
            entity.getReason(),
            entity.getCreatedAt()
        );
    }
}
