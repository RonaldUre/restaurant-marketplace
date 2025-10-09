// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/application/factory/OrderFactory.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.factory;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.CatalogPricingPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.InventoryPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.PaymentsPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.Order;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderLine;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.CustomerId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Quantity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application-level factory para ORDERING:
 * - Ensambla el agregado {@link Order} a partir del resultado de pricing.
 * - Genera objetos para los puertos (reservas de inventario, charge request).
 * - Provee utilidades para construir resúmenes de notificación.
 *
 * Sin dependencias de JPA ni web; solo dominio y puertos.
 */
@Component
public class OrderFactory {

    /**
     * Crea un nuevo {@link Order} en estado CREATED usando el snapshot de
     * precios/líneas
     * que viene de {@link CatalogPricingPort}.
     */
    public Order newFrom(TenantId tenantId,
            UserId customerUserId,
            CatalogPricingPort.PricedCatalog priced) {

        var customerId = CustomerId.from(customerUserId);
        var lines = priced.lines().stream()
                .map(this::toOrderLine)
                .collect(Collectors.toList());

        Money total = priced.total();
        return Order.create(tenantId, customerId, lines, total);
    }

    /**
     * Convierte las líneas con qty del pricing en reservas para Inventory.
     * Úsalo en la Tx A (reserva) solo si priced.requiresInventory() es true.
     */
    public List<InventoryPort.Reservation> reservationsFrom(CatalogPricingPort.PricedCatalog priced) {
        return priced.lines().stream()
                .map(l -> new InventoryPort.Reservation(l.productId(), l.qty()))
                .collect(Collectors.toList());
    }

    /**
     * Construye el request de cobro para la pasarela. Se llama fuera de
     * transacción.
     */
    public PaymentsPort.CreatePaymentRequest toCreatePaymentRequest(Order order, String paymentMethod) {
        return new PaymentsPort.CreatePaymentRequest(
                order.id().value(),
                order.tenantId(),
                order.total(),
                paymentMethod);
    }

    /**
     * 👇 AÑADE ESTE MÉTODO 👇
     * Convierte las líneas de un agregado Order existente en reservas para
     * Inventory.
     * Úsalo en la lógica de pago para saber qué confirmar o liberar.
     */
    public List<InventoryPort.Reservation> reservationsFromDomain(Order order) {
        return order.lines().stream()
                .map(line -> new InventoryPort.Reservation(line.productId(), line.qty().value()))
                .collect(Collectors.toList());
    }

    /**
     * Pequeño resumen para emails/notificaciones (p.ej. "2 items · $24.00").
     * Ajusta a tu gusto o mueve a un mapper de view si prefieres.
     */
    public String buildSummary(Order order) {
        int items = order.lines().stream().mapToInt(l -> l.qty().value()).sum();
        return items + " items · " + order.total();
    }

    // ---------- helpers internos ----------

    private OrderLine toOrderLine(CatalogPricingPort.Line line) {
        return OrderLine.of(
                line.productId(),
                line.name(),
                line.unitPrice(),
                Quantity.of(line.qty()),
                line.lineTotal());
    }
}
