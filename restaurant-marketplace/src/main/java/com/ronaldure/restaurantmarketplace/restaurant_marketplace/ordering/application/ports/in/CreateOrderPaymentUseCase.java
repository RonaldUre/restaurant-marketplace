// ordering/application/ports/in/CreateOrderPaymentUseCase.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.CreateOrderPaymentCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.ApprovalLinkView;

public interface CreateOrderPaymentUseCase {
    /**
     * Inicia un pago y devuelve el enlace de aprobación de la pasarela.
     */
    ApprovalLinkView create(CreateOrderPaymentCommand command);
}
