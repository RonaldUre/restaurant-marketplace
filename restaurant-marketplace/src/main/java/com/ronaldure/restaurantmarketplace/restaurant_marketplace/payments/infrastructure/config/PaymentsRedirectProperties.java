package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payments.redirect")
public record PaymentsRedirectProperties(
        String success,
        String cancel
) {}
