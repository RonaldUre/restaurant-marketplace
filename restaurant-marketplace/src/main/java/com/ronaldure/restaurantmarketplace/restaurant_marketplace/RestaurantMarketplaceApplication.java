package com.ronaldure.restaurantmarketplace.restaurant_marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.config.PayPalProperties;

@SpringBootApplication
@EnableConfigurationProperties(PayPalProperties.class)
public class RestaurantMarketplaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantMarketplaceApplication.class, args);
	}

}
