// notifications/infrastructure/adapter/TwilioSmsAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.application.service.NotificationService;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.NotificationPort;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

//@Component
public class TwilioSmsAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(TwilioSmsAdapter.class);

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.sms-number}") // <-- Leemos el nuevo número de SMS
    private String fromNumber;

    private final NotificationService composer;

    public TwilioSmsAdapter(NotificationService composer) {
        this.composer = composer;
    }

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    // Los métodos públicos son idénticos, solo llaman al composer y a sendMessage
    @Override
    public void sendOrderConfirmed(String to, long orderId, String restaurantName, String summary) {
        NotificationService.Message message = composer.composeOrderConfirmed(to, orderId, restaurantName, summary);
        sendMessage(to, message.body());
    }

    @Override
    public void sendOrderCancelled(String to, long orderId, String reason) {
        NotificationService.Message message = composer.composeOrderCancelled(to, orderId, reason);
        sendMessage(to, message.body());
    }

    @Override
    public void sendPaymentFailed(String to, long orderId, String reason) {
        NotificationService.Message message = composer.composePaymentFailed(to, orderId, reason);
        sendMessage(to, message.body());
    }

    private void sendMessage(String to, String body) {
        // Para la prueba, hardcodea tu número de teléfono verificado en Twilio
        String customerPhone = "+584241391701"; // Formato E.164: +584121234567

        try {
            // LA ÚNICA DIFERENCIA ES AQUÍ: no usamos el prefijo "whatsapp:"
            Message message = Message.creator(
                    new PhoneNumber(customerPhone),      // Número del destinatario
                    new PhoneNumber(fromNumber),         // Tu número de Twilio para SMS
                    body)
                .create();

            log.info("[notifications][SMS_SENT] SID: {}", message.getSid());
        } catch (Exception e) {
            log.error("[notifications][SMS_FAILED] Error sending SMS to {}: {}", customerPhone, e.getMessage());
        }
    }
}