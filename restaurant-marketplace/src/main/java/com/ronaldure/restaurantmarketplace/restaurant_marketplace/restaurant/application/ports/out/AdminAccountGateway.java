package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out;

public interface  AdminAccountGateway {

    void createTenantAdmin(Long tenantId, String email, String rawPassword);
}
