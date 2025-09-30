package com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.application.service;

import org.springframework.stereotype.Service;

/**
 * Centraliza la construcción de subjects y bodies.
 * Los adapters (Console/SMTP) pueden reutilizar este servicio para formatear los mensajes.
 */
@Service
public class NotificationService {

    public Message composeOrderConfirmed(String toEmail, long orderId, String restaurantName, String summary) {
        String subject = "[Order Confirmed] #" + orderId;
        String body = """
                ¡Gracias por tu pedido!
                
                Pedido: #%d
                Restaurante: %s
                Resumen: %s
                
                Te avisaremos cuando esté listo.
                """.formatted(orderId, safe(restaurantName), safe(summary));
        return new Message(toEmail, subject, body);
    }

    public Message composeOrderCancelled(String toEmail, long orderId, String reason) {
        String subject = "[Order Cancelled] #" + orderId;
        String body = """
                Tu pedido #%d ha sido cancelado.
                Motivo: %s
                
                Si no reconoces esta acción, responde a este mensaje.
                """.formatted(orderId, safe(reason));
        return new Message(toEmail, subject, body);
    }

    public Message composePaymentFailed(String toEmail, long orderId, String reason) {
        String subject = "[Payment Failed] Pedido #%d".formatted(orderId);
        String body = """
                No pudimos procesar el pago del pedido #%d.
                Motivo: %s
                
                Intenta nuevamente o usa otro método de pago.
                """.formatted(orderId, safe(reason));
        return new Message(toEmail, subject, body);
    }

    private String safe(String s) { return s == null ? "" : s; }

    /** DTO simple para transportar el correo listo para enviar. */
    public record Message(String to, String subject, String body) { }
}
