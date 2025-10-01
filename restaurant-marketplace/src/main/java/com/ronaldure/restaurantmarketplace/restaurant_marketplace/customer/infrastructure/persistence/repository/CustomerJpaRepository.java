package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.persistence.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.persistence.entity.JpaCustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerJpaRepository extends JpaRepository<JpaCustomerEntity, Long> {

    Optional<JpaCustomerEntity> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    // opcional si necesitas chequear unicidad de phone también
    boolean existsByPhone(String phone);
}
