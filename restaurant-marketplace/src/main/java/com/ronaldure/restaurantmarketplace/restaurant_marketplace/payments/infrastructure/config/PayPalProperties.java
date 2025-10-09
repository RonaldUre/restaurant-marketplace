package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "paypal.sandbox")
public record PayPalProperties(String clientId, String clientSecret) {
}
