package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.events;

public interface DomainEventPublisher {
  void publish(DomainEvent event);
}
