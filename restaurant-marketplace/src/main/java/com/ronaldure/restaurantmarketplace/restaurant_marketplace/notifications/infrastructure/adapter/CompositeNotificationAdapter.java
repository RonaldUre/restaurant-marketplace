// notifications/infrastructure/adapter/CompositeNotificationAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.NotificationPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Primary // <-- ¡Esta anotación es la clave!
public class CompositeNotificationAdapter implements NotificationPort {

    private final List<NotificationPort> adapters;

    public CompositeNotificationAdapter(List<NotificationPort> adapters) {
        // Filtramos para evitar que este adaptador se llame a sí mismo en un bucle infinito
        this.adapters = adapters.stream()
                .filter(adapter -> !(adapter instanceof CompositeNotificationAdapter))
                .toList();
    }

    @Override
    public void sendOrderConfirmed(String toEmail, long orderId, String restaurantName, String summary) {
        // Llamamos al método en cada uno de los adaptadores registrados
        for (NotificationPort adapter : adapters) {
            try {
                // Aquí podrías usar el 'toEmail' para buscar el teléfono del cliente si es necesario
                adapter.sendOrderConfirmed(toEmail, orderId, restaurantName, summary);
            } catch (Exception e) {
                // Loguear el error de un canal pero continuar con los demás
                System.err.println("Error en el adaptador " + adapter.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    @Override
    public void sendOrderCancelled(String toEmail, long orderId, String reason) {
        for (NotificationPort adapter : adapters) {
            try {
                adapter.sendOrderCancelled(toEmail, orderId, reason);
            } catch (Exception e) {
                System.err.println("Error en el adaptador " + adapter.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    @Override
    public void sendPaymentFailed(String toEmail, long orderId, String reason) {
        for (NotificationPort adapter : adapters) {
            try {
                adapter.sendPaymentFailed(toEmail, orderId, reason);
            } catch (Exception e) {
                System.err.println("Error en el adaptador " + adapter.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }
}