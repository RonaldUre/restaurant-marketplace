// notifications/infrastructure/adapter/TwilioWhatsAppAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.application.service.NotificationService;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.NotificationPort;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class TwilioWhatsAppAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(TwilioWhatsAppAdapter.class);

    private final String accountSid;
    private final String authToken;
    private final String fromNumber;
    private final boolean sandboxEnabled;
    private final String sandboxRecipient;

    private final NotificationService composer;

    public TwilioWhatsAppAdapter(
            NotificationService composer,
            @Value("${twilio.account-sid}") String accountSid,
            @Value("${twilio.auth-token}") String authToken,
            @Value("${twilio.sandbox-number}") String fromNumber,
            @Value("${twilio.sandbox.enabled:false}") boolean sandboxEnabled,
            @Value("${notifications.whatsapp.sandbox-recipient:}") String sandboxRecipient) {
        this.composer = composer;
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromNumber = fromNumber;
        this.sandboxEnabled = sandboxEnabled;
        this.sandboxRecipient = sandboxRecipient;
    }

    @PostConstruct
    public void init() {
        if (sandboxEnabled && (sandboxRecipient == null || sandboxRecipient.isBlank())) {
            throw new IllegalStateException(
                "twilio.sandbox.enabled=true pero notifications.whatsapp.sandbox-recipient está vacío");
        }
        Twilio.init(accountSid, authToken);
        log.info("Twilio inicializado. Sandbox: {}", sandboxEnabled);
    }

    @Override
    public void sendOrderConfirmed(String to, long orderId, String restaurantName, String summary) {
        NotificationService.Message msg = composer.composeOrderConfirmed(to, orderId, restaurantName, summary);
        sendMessage(resolveRecipient(to), msg.body());
    }

    @Override
    public void sendOrderCancelled(String to, long orderId, String reason) {
        NotificationService.Message msg = composer.composeOrderCancelled(to, orderId, reason);
        sendMessage(resolveRecipient(to), msg.body());
    }

    @Override
    public void sendPaymentFailed(String to, long orderId, String reason) {
        NotificationService.Message msg = composer.composePaymentFailed(to, orderId, reason);
        sendMessage(resolveRecipient(to), msg.body());
    }

    private String resolveRecipient(String to) {
        if (sandboxEnabled) {
            return sandboxRecipient; // en sandbox siempre enviamos al verificado
        }
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("El destinatario 'to' es obligatorio en producción");
        }
        return to;
    }

    private void sendMessage(String to, String body) {
        // Asegura formato WhatsApp
        String toWhatsApp = to.startsWith("whatsapp:") ? to : "whatsapp:" + to;
        String fromWhatsApp = fromNumber.startsWith("whatsapp:") ? fromNumber : "whatsapp:" + fromNumber;

        try {
            Message message = Message.creator(
                    new PhoneNumber(toWhatsApp),
                    new PhoneNumber(fromWhatsApp),
                    body
            ).create();

            log.info("[notifications][WHATSAPP_SENT] SID: {}", message.getSid());
        } catch (Exception e) {
            log.error("[notifications][WHATSAPP_FAILED] Error sending to {}: {}", to, e.getMessage(), e);
        }
    }
}