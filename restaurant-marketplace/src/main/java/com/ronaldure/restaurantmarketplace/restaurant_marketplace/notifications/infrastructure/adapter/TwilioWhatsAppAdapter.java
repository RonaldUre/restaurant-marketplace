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

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.sandbox-number}")
    private String fromNumber;

    private final NotificationService composer; // Ahora sí lo usaremos

    public TwilioWhatsAppAdapter(NotificationService composer) {
        this.composer = composer;
    }

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    @Override
    public void sendOrderConfirmed(String to, long orderId, String restaurantName, String summary) {
        // ✅ CORRECTO: Usamos el composer para crear el mensaje
        NotificationService.Message message = composer.composeOrderConfirmed(to, orderId, restaurantName, summary);
        sendMessage(to, message.body());
    }

    @Override
    public void sendOrderCancelled(String to, long orderId, String reason) {
        // ✅ CORRECTO: Usamos el composer para crear el mensaje
        NotificationService.Message message = composer.composeOrderCancelled(to, orderId, reason);
        sendMessage(to, message.body());
    }

    @Override
    public void sendPaymentFailed(String to, long orderId, String reason) {
        // ✅ CORRECTO: Usamos el composer para crear el mensaje
        NotificationService.Message message = composer.composePaymentFailed(to, orderId, reason);
        sendMessage(to, message.body());
    }

    private void sendMessage(String to, String body) {
        // Asumimos que 'to' es el email. Necesitarías un servicio para buscar el teléfono.
        // Para la prueba, puedes hardcodear tu número de teléfono aquí.
        // Ejemplo: String customerPhone = "+584121234567";
        String customerPhone = "+584241391701"; 
        
        try {
            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + customerPhone),
                    new PhoneNumber("whatsapp:" + fromNumber),
                    body)
                .create();

            log.info("[notifications][WHATSAPP_SENT] SID: {}", message.getSid());
        } catch (Exception e) {
            log.error("[notifications][WHATSAPP_FAILED] Error sending message to {}: {}", customerPhone, e.getMessage());
        }
    }
}