package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo.CustomerId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo.Email;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo.Name;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.domain.model.vo.Phone;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate Root: Customer
 *
 * Responsibilities:
 * - Enforce invariants for customer data (email, name, optional phone).
 * - Model atomic operations: register, update profile, change password, archive.
 * - Keep domain independent from persistence and web.
 *
 * Notes:
 * - Password hashing MUST be done outside; domain only stores the resulting hash.
 * - No tenantId here; customers are global to the marketplace.
 */
public final class Customer {

    // Technical identity (assigned by persistence)
    private CustomerId id; // nullable before persistence

    // Core attributes
    private Email email;
    private Name name;
    private Phone phone;          // optional VO (may be empty)
    private String passwordHash;  // already hashed string

    // Lifecycle timestamps (optional but handy)
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt; // null => active

    private Customer(
            CustomerId id,
            Email email,
            Name name,
            Phone phone,
            String passwordHash,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        this.id = id;
        this.email = Objects.requireNonNull(email, "email is required");
        this.name = Objects.requireNonNull(name, "name is required");
        this.phone = Objects.requireNonNullElseGet(phone, Phone::empty);
        this.passwordHash = requirePasswordHash(passwordHash);
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
        this.updatedAt = Objects.requireNonNullElseGet(updatedAt, Instant::now);
        this.deletedAt = deletedAt;
        validateInvariants();
    }

    /** Factory: register new customer (requires pre-hashed password). */
    public static Customer register(Email email, Name name, Phone phone, String passwordHash) {
        return new Customer(
                null,
                email,
                name,
                phone,
                passwordHash,
                Instant.now(),
                Instant.now(),
                null
        );
    }

    /** Rehydrate from persistence layer without altering timestamps/flags. */
    public static Customer rehydrate(
            CustomerId id,
            Email email,
            Name name,
            Phone phone,
            String passwordHash,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        return new Customer(
                Objects.requireNonNull(id, "id is required"),
                email,
                name,
                phone,
                passwordHash,
                createdAt,
                updatedAt,
                deletedAt
        );
    }

    /** Assign ID once after persistence. */
    public void assignId(CustomerId id) {
        if (this.id != null) throw new IllegalStateException("Customer id is already assigned");
        this.id = Objects.requireNonNull(id, "id is required");
    }

    /** Update mutable profile fields in one atomic operation. */
    public void updateProfile(Name name, Phone phone) {
        if (isArchived()) throw new IllegalStateException("Archived customers cannot be updated");
        this.name = Objects.requireNonNull(name, "name is required");
        this.phone = Objects.requireNonNullElseGet(phone, Phone::empty);
        validateInvariants();
        touch();
    }

    /** Change password with a NEW pre-hashed value. */
    public void changePasswordHash(String newHash) {
        if (isArchived()) throw new IllegalStateException("Archived customers cannot change password");
        this.passwordHash = requirePasswordHash(newHash);
        touch();
    }

    /** Soft-delete semantics to align with potential deleted_at column. */
    public void archive() {
        if (!isArchived()) {
            this.deletedAt = Instant.now();
            touch();
        }
    }

    public boolean isArchived() { return this.deletedAt != null; }

    private void validateInvariants() {
        if (email == null) throw new IllegalStateException("email must be present");
        if (name == null) throw new IllegalStateException("name must be present");
        if (passwordHash == null || passwordHash.isBlank())
            throw new IllegalStateException("passwordHash must be present");
    }

    private String requirePasswordHash(String hash) {
        String v = Objects.requireNonNull(hash, "passwordHash is required").trim();
        if (v.isEmpty()) throw new IllegalArgumentException("passwordHash cannot be blank");
        if (v.length() > 255) throw new IllegalArgumentException("passwordHash length must be <= 255");
        return v;
    }

    private void touch() { this.updatedAt = Instant.now(); }

    // -------- Getters (no setters; aggregate controls mutations) --------
    public CustomerId id() { return id; }
    public Email email() { return email; }
    public Name name() { return name; }
    public Phone phone() { return phone; }
    public String passwordHash() { return passwordHash; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public Instant deletedAt() { return deletedAt; }

    // Equality based on technical id if present; otherwise on email (business identity)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer other = (Customer) o;
        if (this.id != null && other.id != null) return this.id.equals(other.id);
        return this.email.equals(other.email);
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : email.hashCode();
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + (id != null ? id.value() : "null") +
                ", email=" + email +
                ", name=" + name +
                ", phone=" + phone +
                ", archived=" + isArchived() +
                '}';
    }
}
