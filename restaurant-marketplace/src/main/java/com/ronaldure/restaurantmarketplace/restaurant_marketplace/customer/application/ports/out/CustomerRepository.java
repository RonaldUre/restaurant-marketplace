package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.Customer;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo.Email;

import java.util.Optional;

/**
 * Puerto de persistencia para Customer (aggregate).
 * - save: persiste nuevo agregado y asigna id.
 * - update: guarda cambios en un agregado existente.
 * - findById / findByEmail: lecturas típicas.
 * - existsByEmail: verificación rápida de unicidad (además del UNIQUE en DB).
 *
 * Nota: la unicidad la garantiza la base de datos (UNIQUE email). En service
 * puedes usar existsByEmail para feedback temprano y luego capturar el
 * DataIntegrityViolationException como defensa final.
 */
public interface CustomerRepository {

    /** Inserta un nuevo Customer (id debe ser null en el aggregate). Retorna el aggregate con id asignado. */
    Customer save(Customer customer);

    /** Actualiza un Customer existente (por id). */
    void update(Customer customer);

    Optional<Customer> findById(Long id);

    Optional<Customer> findByEmail(Email email);

    boolean existsByEmail(Email email);
}
