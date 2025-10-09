package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.ConfirmPaymentCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;

public interface ConfirmPaymentUseCase {
    OrderDetailView confirm(ConfirmPaymentCommand command);
}
