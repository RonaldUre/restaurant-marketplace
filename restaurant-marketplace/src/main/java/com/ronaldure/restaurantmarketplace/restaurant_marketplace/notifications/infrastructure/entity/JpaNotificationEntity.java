package com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.entity;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.domain.NotificationStatus;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.domain.NotificationType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(
    name = "notification_logs",
    indexes = {
        @Index(name = "idx_notifications_tenant_created", columnList = "tenant_id, created_at"),
        @Index(name = "idx_notifications_tenant_status_created", columnList = "tenant_id, status, created_at"),
        @Index(name = "idx_notifications_order", columnList = "order_id"),
        @Index(name = "idx_notifications_type", columnList = "type")
    }
)
public class JpaNotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Aislamiento multitenant por clave simple (sin relaciones para no acoplar módulos)
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status;

    @Column(name = "attempts", nullable = false)
    private Integer attempts;

    @Column(name = "to_email", length = 255)
    private String toEmail;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Lob
    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "last_error", length = 500)
    private String lastError;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_attempt_at")
    private Instant lastAttemptAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    // ---------- lifecycle ----------
    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (attempts == null) attempts = 0;
        if (status == null) status = NotificationStatus.PENDING;
    }

    // ---------- getters/setters ----------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }

    public Integer getAttempts() { return attempts; }
    public void setAttempts(Integer attempts) { this.attempts = attempts; }

    public String getToEmail() { return toEmail; }
    public void setToEmail(String toEmail) { this.toEmail = toEmail; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getLastError() { return lastError; }
    public void setLastError(String lastError) { this.lastError = lastError; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getLastAttemptAt() { return lastAttemptAt; }
    public void setLastAttemptAt(Instant lastAttemptAt) { this.lastAttemptAt = lastAttemptAt; }

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }

    // ---------- equals/hashCode por id ----------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaNotificationEntity that)) return false;
        return id != null && Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return 31; }
}
