package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.config;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ PayPalProperties.class, PaymentsRedirectProperties.class })
public class PayPalConfig {

    @Bean
    public PayPalHttpClient payPalHttpClient(PayPalProperties props) {
        PayPalEnvironment environment = new PayPalEnvironment.Sandbox(
                props.clientId(),
                props.clientSecret()
        );
        return new PayPalHttpClient(environment);
    }
}