// ordering/application/ports/in/CaptureOrderPaymentUseCase.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.CaptureOrderPaymentCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;

public interface CaptureOrderPaymentUseCase {
    /**
     * Captura un pago previamente aprobado y actualiza la orden.
     */
    OrderDetailView capture(CaptureOrderPaymentCommand command);
}