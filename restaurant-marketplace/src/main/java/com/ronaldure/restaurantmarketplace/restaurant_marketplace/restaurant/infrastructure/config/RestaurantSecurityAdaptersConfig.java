package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.config;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.security.AccessControlImpl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentUserProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestaurantSecurityAdaptersConfig {

    @Bean
    public AccessControl accessControl(CurrentUserProvider currentUserProvider) {
        return new AccessControlImpl(currentUserProvider);
    }
}