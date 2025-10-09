package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.events;

import java.time.Instant;

public interface DomainEvent {
  Instant occurredOn();
}
