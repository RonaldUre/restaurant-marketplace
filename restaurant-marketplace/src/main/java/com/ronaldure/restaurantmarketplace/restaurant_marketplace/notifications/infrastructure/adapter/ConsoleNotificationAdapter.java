package com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.application.service.NotificationService;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.domain.NotificationStatus;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.domain.NotificationType;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.entity.JpaNotificationEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.repository.NotificationJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Adapter MVP que "envía" por consola.
 * - Nunca lanza excepciones hacia Ordering.
 * - Persiste un log en notification_logs con estado PENDING→SENT/FAILED.
 * - Aislado en una tx nueva para no depender de la tx de Ordering.
 */
//@Component
public class ConsoleNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(ConsoleNotificationAdapter.class);

    private static final int SUBJECT_MAX = 255;
    private static final int LAST_ERROR_MAX = 500;
    // Si quieres limitar body en logs/DB, ajusta aquí (0 = sin límite)
    //private static final int BODY_TRUNCATE_FOR_LOG = 0;

    private final NotificationService composer;
    private final NotificationJpaRepository repo;
    private final OrderTenantLookupAdapter orderTenantLookup;

    public ConsoleNotificationAdapter(NotificationService composer,
            NotificationJpaRepository repo,
            OrderTenantLookupAdapter orderTenantLookup) {
        this.composer = composer;
        this.repo = repo;
        this.orderTenantLookup = orderTenantLookup;
    }

    @Override
    public void sendOrderConfirmed(String toEmail, long orderId, String restaurantName, String summary) {
        NotificationService.Message m = composer.composeOrderConfirmed(toEmail, orderId, restaurantName, summary);
        sendAndLog(NotificationType.ORDER_CONFIRMED, m, orderId);
    }

    @Override
    public void sendOrderCancelled(String toEmail, long orderId, String reason) {
        NotificationService.Message m = composer.composeOrderCancelled(toEmail, orderId, reason);
        sendAndLog(NotificationType.ORDER_CANCELLED, m, orderId);
    }

    @Override
    public void sendPaymentFailed(String toEmail, long orderId, String reason) {
        NotificationService.Message m = composer.composePaymentFailed(toEmail, orderId, reason);
        sendAndLog(NotificationType.PAYMENT_FAILED, m, orderId);
    }

    // ---------- helpers ----------

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void sendAndLog(NotificationType type, NotificationService.Message m, long orderId) {
        Long tenantId = orderTenantLookup.resolveTenantIdOrThrow(orderId);

        JpaNotificationEntity e = new JpaNotificationEntity();
        e.setTenantId(tenantId);
        e.setOrderId(orderId);
        e.setType(type);
        e.setStatus(NotificationStatus.PENDING);
        e.setSubject(safeTruncate(m.subject(), SUBJECT_MAX));
        e.setBody(m.body()); // si quieres limitar tamaño en DB, aplica truncate aquí
        e.setToEmail(nullIfBlank(m.to()));
        e.setAttempts(1);
        e.setLastAttemptAt(Instant.now());
        // createdAt via @PrePersist

        try {
            // Validación mínima: destinatario
            if (e.getToEmail() == null) {
                e.setStatus(NotificationStatus.FAILED);
                e.setLastError("missing_recipient");
                repo.save(e);
                log.warn("[notifications][FAILED] type={} orderId={} tenantId={} error={}",
                        type, orderId, tenantId, e.getLastError());
                return;
            }

            // "Envío" por consola
            String bodyForLog = e.getBody();

            log.info("""
                    [notifications][SENDING]
                    to={}
                    subject={}
                    body=
                    {}
                    """, e.getToEmail(), e.getSubject(), bodyForLog);

            // Consideramos enviado
            e.setStatus(NotificationStatus.SENT);
            e.setSentAt(Instant.now());
            e.setLastError(null);
            repo.save(e);

            log.info("[notifications][SENT] type={} orderId={} tenantId={} to={}",
                    type, orderId, tenantId, e.getToEmail());

        } catch (Exception ex) {
            // Nunca propagar a Ordering
            e.setStatus(NotificationStatus.FAILED);
            e.setLastError(safeTruncate(ex.getMessage(), LAST_ERROR_MAX));
            repo.save(e);

            log.error("[notifications][FAILED] type={} orderId={} tenantId={} error={}",
                    type, orderId, tenantId, e.getLastError());
        }
    }

    private static String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private static String safeTruncate(String s, int max) {
        if (s == null || max <= 0)
            return s;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
