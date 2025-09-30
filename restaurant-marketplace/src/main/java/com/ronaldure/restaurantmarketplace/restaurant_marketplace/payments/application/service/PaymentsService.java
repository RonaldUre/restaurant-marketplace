package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.domain.PaymentTransaction;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.application.view.PaymentTransactionView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.repository.PaymentTransactionJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.entity.JpaPaymentTransactionEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class PaymentsService {

    private final PaymentTransactionJpaRepository repo;

    public PaymentsService(PaymentTransactionJpaRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public PaymentTransactionView recordApproved(OrderId orderId, TenantId tenantId, Money amount, String method, String txId) {
        PaymentTransaction tx = PaymentTransaction.approved(orderId, tenantId, amount, method, txId);
        save(tx);
        return toView(tx);
    }

    @Transactional
    public PaymentTransactionView recordDeclined(OrderId orderId, TenantId tenantId, Money amount, String method, String reason) {
        PaymentTransaction tx = PaymentTransaction.declined(orderId, tenantId, amount, method, reason);
        save(tx);
        return toView(tx);
    }

    @Transactional
    public PaymentTransactionView recordInitiated(OrderId orderId, TenantId tenantId, Money amount, String method) {
        PaymentTransaction tx = PaymentTransaction.initiated(orderId, tenantId, amount, method);
        save(tx);
        return toView(tx);
    }

    private void save(PaymentTransaction tx) {
        // Mapper simple in-line (puedes mover a PaymentsPersistenceMapper si ya lo tienes)
        JpaPaymentTransactionEntity entity = new JpaPaymentTransactionEntity();
        entity.setOrderId(tx.orderId().value());
        entity.setTenantId(tx.tenantId().value());
        entity.setAmount(amountOf(tx.amount()));
        entity.setCurrency(currencyOf(tx.amount()));
        entity.setMethod(tx.method());
        entity.setStatus(tx.status().name());
        entity.setTxId(tx.txId());
        entity.setReason(tx.reason());
        entity.setCreatedAt(Instant.now());
        repo.save(entity);
    }

    private PaymentTransactionView toView(PaymentTransaction tx) {
        return PaymentTransactionView.of(
                tx.orderId().value(),
                tx.tenantId().value(),
                amountOf(tx.amount()),
                currencyOf(tx.amount()),
                tx.method(),
                tx.status().name(),
                tx.txId(),
                tx.reason(),
                tx.createdAt()
        );
    }

    // Helpers para no acoplar a getters internos del VO Money si aún no existen
    private BigDecimal amountOf(Money m) {
        try {
            return (BigDecimal) Money.class.getMethod("amount").invoke(m);
        } catch (Exception e) {
            // Fallback: muy básico; idealmente agrega getters a Money
            return new BigDecimal(String.valueOf(m).replaceAll("[^0-9.,-]", "").replace(",", "."));
        }
    }

    private String currencyOf(Money m) {
        try {
            return (String) Money.class.getMethod("currency").invoke(m);
        } catch (Exception e) {
            return "USD";
        }
    }
}
