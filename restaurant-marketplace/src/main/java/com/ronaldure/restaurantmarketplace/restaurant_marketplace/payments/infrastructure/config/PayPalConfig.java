package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.config;


// .../restaurant_marketplace/shared/infrastructure/config/PayPalConfig.java
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalConfig {

    // Spring inyectará automáticamente las propiedades que creamos en el paso 3
    @Bean
    public PayPalHttpClient payPalHttpClient(PayPalProperties props) {
        PayPalEnvironment environment = new PayPalEnvironment.Sandbox(
                props.clientId(),
                props.clientSecret()
        );
        return new PayPalHttpClient(environment);
    }
}