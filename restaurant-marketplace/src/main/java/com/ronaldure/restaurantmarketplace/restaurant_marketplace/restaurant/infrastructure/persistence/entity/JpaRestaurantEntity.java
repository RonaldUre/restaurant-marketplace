// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/infrastructure/persistence/entity/JpaRestaurantEntity.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * JPA entity for the "restaurants" table.
 * - Keeps pure primitives/Strings to avoid leaking domain VOs into persistence.
 * - Optional fields are nullable; "empty address" is represented by all-null
 * address columns.
 * - openingHoursJson is stored as JSON (or TEXT depending on DB capabilities).
 */
@Entity
@Table(name = "restaurants", uniqueConstraints = {
        @UniqueConstraint(name = "uk_restaurants__slug", columnNames = { "slug" })
}, indexes = {
        @Index(name = "idx_restaurants__status", columnList = "status"),
        @Index(name = "idx_restaurants__city", columnList = "city")
})
public class JpaRestaurantEntity {

    // -------- Identity --------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    // -------- Core profile --------
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "slug", nullable = false, length = 140)
    private String slug;

    /**
     * Stored as VARCHAR to keep the entity infra-only.
     * Values expected: "OPEN" | "CLOSED" | "SUSPENDED"
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // -------- Contact (optional) --------
    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    // -------- Address (all optional) --------
    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "city", length = 120)
    private String city;

    @Column(name = "country", length = 2)
    private String country; // ISO-3166-1 alpha-2

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    // -------- Opening hours (optional) --------
    // If your MySQL supports JSON, keep columnDefinition="json"; otherwise change
    // to TEXT in both entity and Flyway.
    @Column(name = "opening_hours", columnDefinition = "json")
    private String openingHoursJson;

    // -------- Audit --------
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    // -------- Constructors --------
    public JpaRestaurantEntity() {
        // for JPA
    }

    public JpaRestaurantEntity(Long id,
            String name,
            String slug,
            String status,
            String email,
            String phone,
            String addressLine1,
            String addressLine2,
            String city,
            String country,
            String postalCode,
            String openingHoursJson) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.status = status;
        this.email = email;
        this.phone = phone;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
        this.openingHoursJson = openingHoursJson;
    }

    // -------- Getters / Setters --------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getOpeningHoursJson() {
        return openingHoursJson;
    }

    public void setOpeningHoursJson(String openingHoursJson) {
        this.openingHoursJson = openingHoursJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    // -------- Equality (by id) --------
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof JpaRestaurantEntity that))
            return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
