package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.events.DomainEvent;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.events.DomainEventPublisher;

@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {
  private final ApplicationEventPublisher delegate;
  public SpringDomainEventPublisher(ApplicationEventPublisher delegate) { this.delegate = delegate; }
  @Override public void publish(DomainEvent event) { delegate.publishEvent(event); }
}