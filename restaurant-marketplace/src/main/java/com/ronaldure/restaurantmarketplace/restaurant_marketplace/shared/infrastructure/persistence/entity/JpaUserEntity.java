package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class JpaUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=255)
    private String email;

    @Column(name="password_hash", nullable=false, length=100)
    private String passwordHash;

    @Column(nullable=false, length=32)
    private String role; // RESTAURANT_ADMIN | SUPER_ADMIN

    @Column(name="tenant_id")
    private Long tenantId; // null for SUPER_ADMIN

    // Getters/setters (no Lombok)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
}
