// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/ordering/infrastructure/persistence/adapter/CustomerDirectoryJpaAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.CustomerDirectoryPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerDirectoryJpaAdapter implements CustomerDirectoryPort {

    private final JdbcTemplate jdbc;

    public CustomerDirectoryJpaAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public String getCustomerEmail(UserId customerId) {
        try {
            return jdbc.queryForObject(
                    "SELECT email FROM customers WHERE id = ?",
                    String.class,
                    Long.parseLong(customerId.value())
            );
        } catch (EmptyResultDataAccessException e) {
            return null; // or throw an application-level exception if you prefer
        }
    }
}
