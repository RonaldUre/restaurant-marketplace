package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security.RefreshTokenCleanupProperties;

/**
 * Enables Spring's scheduled task execution.
 * Guarded by a property so we can turn it off when needed (tests, local, etc.).
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties(RefreshTokenCleanupProperties.class)
@ConditionalOnProperty(name = "jobs.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulingConfig {
    // No beans needed here; just flips the scheduling switch on.
}
