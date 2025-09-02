// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/infrastructure/config/RestaurantConfiguration.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Restaurant module configuration.
 *
 * Hoy no define beans porque el módulo se arma por auto-detección de componentes
 * (services, adapters, repos, mappers, controllers).
 *
 * Deja este "hook" para:
 *  - registrar beans propios del módulo en el futuro,
 *  - configurar conversores, validadores o interceptores específicos,
 *  - aislar wiring si algún día desactivas component-scan y prefieres ensamblado explícito.
 */
@Configuration
public class RestaurantConfiguration {
    // Beans del módulo (si se requieren en el futuro) irán aquí.
}
