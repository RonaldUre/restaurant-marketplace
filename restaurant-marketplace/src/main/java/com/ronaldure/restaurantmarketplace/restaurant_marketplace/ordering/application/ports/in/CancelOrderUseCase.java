package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.command.CancelOrderCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;

public interface CancelOrderUseCase {
    OrderDetailView cancel(CancelOrderCommand command);
}
