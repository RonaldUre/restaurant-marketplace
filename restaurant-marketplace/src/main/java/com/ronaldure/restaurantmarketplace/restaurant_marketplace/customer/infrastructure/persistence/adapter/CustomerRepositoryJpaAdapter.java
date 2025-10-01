package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.ports.out.CustomerRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.Customer;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo.CustomerId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo.Email;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo.Name;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo.Phone;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.persistence.entity.JpaCustomerEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.persistence.repository.CustomerJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class CustomerRepositoryJpaAdapter implements CustomerRepository {

    private final CustomerJpaRepository jpa;

    public CustomerRepositoryJpaAdapter(CustomerJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public Customer save(Customer customer) {
        JpaCustomerEntity entity = toEntity(customer);
        JpaCustomerEntity saved = jpa.save(entity);

        if (customer.id() == null && saved.getId() != null) {
            customer.assignId(CustomerId.of(saved.getId()));
        }
        return toDomain(saved);
    }

    @Override
    @Transactional
    public void update(Customer customer) {
        save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findByEmail(Email email) {
        return jpa.findByEmailIgnoreCase(email.value()).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(Email email) {
        return jpa.existsByEmail(email.value());
    }

    // -------- mapping --------
    private JpaCustomerEntity toEntity(Customer c) {
        return new JpaCustomerEntity(
                c.id() != null ? c.id().value() : null,
                c.email().value(),
                c.name().value(),
                c.phone().isEmpty() ? null : c.phone().value(), // NULL si no hay teléfono
                c.passwordHash(),
                c.createdAt(),
                c.updatedAt()
        );
    }

    private Customer toDomain(JpaCustomerEntity e) {
        return Customer.rehydrate(
                e.getId() != null ? CustomerId.of(e.getId()) : null,
                Email.of(e.getEmail()),
                Name.of(e.getName()),
                (e.getPhone() == null || e.getPhone().isBlank()) ? Phone.empty() : Phone.of(e.getPhone()),
                e.getPasswordHash(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                null // deletedAt: tu esquema no lo maneja; queda siempre null en dominio
        );
    }
}
