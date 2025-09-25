package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.events;

import java.time.Instant;

public final class ProductCreatedEvent implements DomainEvent {
  private final long tenantId;
  private final long productId;
  private final Instant occurredOn = Instant.now();

  public ProductCreatedEvent(long tenantId, long productId) {
    if (tenantId <= 0 || productId <= 0) throw new IllegalArgumentException("ids > 0");
    this.tenantId = tenantId;
    this.productId = productId;
  }
  public long tenantId() { return tenantId; }
  public long productId() { return productId; }
  @Override public Instant occurredOn() { return occurredOn; }
}