package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.Order;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;

import java.util.Optional;

/** Persistencia del agregado Order (comandos). Las consultas usan proyecciones aparte. */
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(OrderId id, TenantId tenantId);
    Optional<Order> findById(OrderId id); 
}
