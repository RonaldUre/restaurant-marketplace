package com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.application.service.NotificationService;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.domain.NotificationStatus;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.domain.NotificationType;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.entity.JpaNotificationEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.repository.NotificationJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Profile("!dev") // Este adaptador se activará en cualquier perfil que NO sea 'dev'
public class SmtpNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SmtpNotificationAdapter.class);

    private final NotificationService composer;
    private final NotificationJpaRepository repo;
    private final OrderTenantLookupAdapter orderTenantLookup;
    private final JavaMailSender mailSender; // Inyectado por Spring Boot

    public SmtpNotificationAdapter(NotificationService composer, NotificationJpaRepository repo,
                                   OrderTenantLookupAdapter orderTenantLookup, JavaMailSender mailSender) {
        this.composer = composer;
        this.repo = repo;
        this.orderTenantLookup = orderTenantLookup;
        this.mailSender = mailSender;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void sendAndLog(NotificationType type, NotificationService.Message m, long orderId) {
        Long tenantId = orderTenantLookup.resolveTenantIdOrThrow(orderId);
        JpaNotificationEntity e = createLogEntity(type, m, orderId, tenantId);

        try {
            if (e.getToEmail() == null) {
                throw new IllegalArgumentException("missing_recipient");
            }

            // Crear y enviar el mensaje de correo
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(e.getToEmail());
            mailMessage.setSubject(e.getSubject());
            mailMessage.setText(e.getBody());
            // mailMessage.setFrom("no-reply@tuapp.com"); // Opcional, Gmail lo sobreescribirá

            mailSender.send(mailMessage);

            // Si el envío fue exitoso, actualizamos el log
            e.setStatus(NotificationStatus.SENT);
            e.setSentAt(Instant.now());
            e.setLastError(null);
            repo.save(e);
            log.info("[notifications][SENT] type={} orderId={} tenantId={} to={}", type, orderId, tenantId, e.getToEmail());

        } catch (Exception ex) {
            // Si algo falla, lo registramos y no propagamos la excepción
            e.setStatus(NotificationStatus.FAILED);
            e.setLastError(safeTruncate(ex.getMessage(), 500));
            repo.save(e);
            log.error("[notifications][FAILED] type={} orderId={} tenantId={} error={}", type, orderId, tenantId, e.getLastError(), ex);
        }
    }
    
    // Métodos helper para crear la entidad de log y truncar, puedes copiarlos de tu ConsoleNotificationAdapter
    private JpaNotificationEntity createLogEntity(NotificationType type, NotificationService.Message m, long orderId, Long tenantId) {
        JpaNotificationEntity e = new JpaNotificationEntity();
        e.setTenantId(tenantId);
        e.setOrderId(orderId);
        e.setType(type);
        e.setStatus(NotificationStatus.PENDING);
        e.setSubject(safeTruncate(m.subject(), 255));
        e.setBody(m.body());
        e.setToEmail(m.to() == null || m.to().isBlank() ? null : m.to());
        e.setAttempts(1);
        e.setLastAttemptAt(Instant.now());
        return e;
    }

    private static String safeTruncate(String s, int max) {
        if (s == null || max <= 0) return s;
        return s.length() <= max ? s : s.substring(0, max);
    }
}