package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo.*;
import java.util.Objects;

/**
 * Aggregate Root for Restaurant (Tenant).
 * Owns invariants and state transitions.
 *
 * Notes:
 * - Immutable VOs protect local invariants.
 * - Aggregate is mutable by design (within transaction boundaries).
 * - Keep persistence concerns (JPA) out of this class.
 */
public class Restaurant {

    // Identity
    private RestaurantId id; // may be null before persistence (creation flow)

    // Core profile
    private Name name;
    private Slug slug;
    private Email email; // optional (may be null)
    private Phone phone; // optional (may be null)
    private Address address; // optional (may be null)
    private OpeningHours openingHours; // optional (may be null)

    // Operational state
    private Status status;

    // -------- Constructors / Factories --------

    private Restaurant(RestaurantId id,
            Name name,
            Slug slug,
            Email email,
            Phone phone,
            Address address,
            OpeningHours openingHours,
            Status status) {
        // Required
        this.id = id; // may be null on new aggregates
        this.name = Objects.requireNonNull(name, "name is required");
        this.slug = Objects.requireNonNull(slug, "slug is required");
        this.status = Objects.requireNonNull(status, "status is required");

        // Optional (may be null)
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.openingHours = openingHours;

        // Invariants that depend on multiple fields could go here if needed
    }

    /**
     * Factory for new Restaurants (pre-persistence, id not assigned yet). Defaults
     * to CLOSED.
     */
    public static Restaurant create(Name name,
            Slug slug,
            Email email,
            Phone phone,
            Address address,
            OpeningHours openingHours) {
        return new Restaurant(
                /* id */ null,
                name, slug, email, phone, address, openingHours,
                Status.CLOSED);
    }

    /** Factory for rehydration (e.g., loading from repository). */
    public static Restaurant rehydrate(RestaurantId id,
            Name name,
            Slug slug,
            Email email,
            Phone phone,
            Address address,
            OpeningHours openingHours,
            Status status) {
        return new Restaurant(id, name, slug, email, phone, address, openingHours, status);
    }

    // -------- Business behavior (commands) --------

    /** Open restaurant if not suspended and not already open. */
    public void open() {
        ensureNotSuspended("Cannot open a suspended restaurant");
        if (this.status == Status.OPEN)
            return; // idempotent
        this.status = Status.OPEN;
    }

    /** Close restaurant if currently open. */
    public void close() {
        ensureNotSuspended("Cannot change state while restaurant is suspended");
        if (this.status == Status.CLOSED)
            return; // idempotent
        // Optional: if there are pending constraints (e.g., active orders) validate
        // here
        this.status = Status.CLOSED;
    }

    /** Suspend restaurant (platform-level action typically). */
    public void suspend() {
        if (this.status == Status.SUSPENDED)
            return; // idempotent
        this.status = Status.SUSPENDED;
    }

    public void unsuspend() {
        if (this.status == Status.SUSPENDED) {
            this.status = Status.CLOSED; // se reactiva pero queda cerrado hasta revisión manual
            return;
        }
        // idempotente: si ya está CLOSED u OPEN, no hace nada
    }

    /**
     * Update basic profile fields (does not change status). Nulls mean "no change".
     */
    public void updateProfile(Name name,
            Slug slug,
            Email email,
            Phone phone,
            Address addressUpdatePayload, // Renombrado para claridad
            OpeningHours openingHours) {
        // Only apply provided fields; VOs already validated
        ensureNotSuspended("Cannot update profile while restaurant is suspended");

        if (name != null)
            this.name = name;
        if (slug != null)
            this.slug = slug;
        if (email != null)
            this.email = email;
        if (phone != null)
            this.phone = phone;

        // --- MODIFICACIÓN CLAVE PARA ADDRESS ---
        if (addressUpdatePayload != null) {
            // Si hay un payload de actualización de dirección (incluso parcial)
            // debemos fusionar sus campos con la dirección actual.

            // Obtener los valores actuales (si existen)
            String currentLine1 = this.address != null ? this.address.line1() : null;
            String currentLine2 = this.address != null ? this.address.line2() : null;
            String currentCity = this.address != null ? this.address.city() : null;
            String currentCountry = this.address != null ? this.address.country() : null;
            String currentPostalCode = this.address != null ? this.address.postalCode() : null;

            // Decidir los nuevos valores: si el payload tiene un valor, usarlo;
            // si el payload tiene null, mantener el valor actual.
            String newLine1 = addressUpdatePayload.line1() != null ? addressUpdatePayload.line1() : currentLine1;
            String newLine2 = addressUpdatePayload.line2() != null ? addressUpdatePayload.line2() : currentLine2;
            String newCity = addressUpdatePayload.city() != null ? addressUpdatePayload.city() : currentCity;
            String newCountry = addressUpdatePayload.country() != null ? addressUpdatePayload.country()
                    : currentCountry;
            String newPostalCode = addressUpdatePayload.postalCode() != null ? addressUpdatePayload.postalCode()
                    : currentPostalCode;

            // Crear un nuevo Address VO con los valores fusionados
            this.address = Address.of(newLine1, newLine2, newCity, newCountry, newPostalCode);
        }
        // Si addressUpdatePayload es null, significa que no se proporcionaron campos de
        // dirección
        // en el comando, por lo que 'this.address' existente no debe cambiar.

        if (openingHours != null)
            this.openingHours = openingHours;
    }
    // -------- Guards --------

    private void ensureNotSuspended(String message) {
        if (this.status == Status.SUSPENDED) {
            throw new IllegalStateException(message);
        }
    }

    // -------- Getters (no setters; mutate through behavior) --------

    public RestaurantId id() {
        return id;
    }

    /**
     * Assign id after persistence (adapter-level). Keep package-private to avoid
     * misuse.
     */
    void assignId(RestaurantId id) {
        if (this.id != null)
            throw new IllegalStateException("id already assigned");
        this.id = Objects.requireNonNull(id, "id cannot be null");
    }

    public Name name() {
        return name;
    }

    public Slug slug() {
        return slug;
    }

    public Email email() {
        return email;
    }

    public Phone phone() {
        return phone;
    }

    public Address address() {
        return address;
    }

    public OpeningHours openingHours() {
        return openingHours;
    }

    public Status status() {
        return status;
    }

    // -------- Optional: domain events hooks (future) --------
    // e.g., recordEvent(new RestaurantOpenedEvent(this.id));
}
