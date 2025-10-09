package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(
    name = "customers",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_customers_email", columnNames = {"email"}),
        @UniqueConstraint(name = "uk_customers_phone", columnNames = {"phone"})
    },
    indexes = {
        @Index(name = "idx_customers_created", columnList = "created_at")
    }
)
public class JpaCustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Email ya normalizado en minúsculas por el VO
    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    // NULL permitido y UNIQUE a nivel de tabla; si quieres “sin teléfono”, usa NULL
    @Column(name = "phone", length = 30, unique = true)
    private String phone;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    // En MySQL son TIMESTAMP con defaults; Creation/UpdateTimestamp funcionan bien también
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public JpaCustomerEntity() { }

    public JpaCustomerEntity(Long id, String email, String name, String phone,
                             String passwordHash, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaCustomerEntity that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }
}
