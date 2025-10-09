package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.application.view;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO de aplicación para exponer/registrar el resultado de una transacción de pago.
 * Útil para logs/admin o debugging. En el MVP no es obligatorio exponerlo por web.
 */
public record PaymentTransactionView(
        Long orderId,
        Long tenantId,
        BigDecimal amount,
        String currency,
        String method,   // e.g. "FAKE", "CARD"
        String status,   // INITIATED | APPROVED | DECLINED
        String txId,     // puede ser null si no aplica
        String reason,   // motivo de rechazo u observación; puede ser null
        Instant createdAt
) {
    public static PaymentTransactionView of(Long orderId, Long tenantId, BigDecimal amount, String currency,
                                            String method, String status, String txId, String reason,
                                            Instant createdAt) {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(createdAt, "createdAt");
        return new PaymentTransactionView(orderId, tenantId, amount, currency, method, status, txId, reason, createdAt);
    }
}
