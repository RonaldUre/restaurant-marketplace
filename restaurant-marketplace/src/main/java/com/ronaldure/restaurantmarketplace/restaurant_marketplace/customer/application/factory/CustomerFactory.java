package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.factory;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.command.RegisterCustomerCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.command.UpdateCustomerProfileCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.Customer;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo.Email;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo.Name;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo.Phone;

import java.util.Objects;

/**
 * Application-level factory:
 * - Construye VOs desde commands.
 * - Crea aggregates para el flujo de registro.
 * - Produce payloads parciales para updates (null => no cambio).
 */
public final class CustomerFactory {
    private CustomerFactory() {}

    /** Crea el aggregate para registro. Recibe el hash ya calculado. */
    public static Customer newFrom(RegisterCustomerCommand cmd, String passwordHash) {
        Objects.requireNonNull(cmd, "cmd");
        return Customer.register(
                Email.of(cmd.email()),
                Name.of(cmd.name()),
                normalizePhoneForCreate(cmd.phone()),
                Objects.requireNonNull(passwordHash, "passwordHash")
        );
    }

    /** Prepara payload parcial para UpdateProfile: null => no cambiar. */
    public static UpdateProfilePayload toUpdatePayload(UpdateCustomerProfileCommand cmd) {
        Objects.requireNonNull(cmd, "cmd");

        Name name = null;
        if (cmd.name() != null) {
            name = Name.of(cmd.name());
        }

        Phone phone = null;
        if (cmd.phone() != null) {
            // Regla: "" => quitar teléfono; texto => Phone.of
            phone = cmd.phone().isBlank() ? Phone.empty() : Phone.of(cmd.phone());
        }

        return new UpdateProfilePayload(name, phone);
    }

    private static Phone normalizePhoneForCreate(String raw) {
        if (raw == null || raw.isBlank()) return Phone.empty();
        return Phone.of(raw);
    }

    /** Payload parcial para update; los campos null significan “no cambiar”. */
    public record UpdateProfilePayload(
            Name name,   // null => no cambiar nombre
            Phone phone  // null => no cambiar; Phone.empty() => quitar teléfono
    ) {}
}
