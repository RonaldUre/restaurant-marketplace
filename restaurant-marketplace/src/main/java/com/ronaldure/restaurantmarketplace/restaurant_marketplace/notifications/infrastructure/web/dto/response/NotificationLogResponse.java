package com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NotificationLogResponse(
        Long id,
        Long tenantId,
        Long orderId,
        String type,          // ORDER_CONFIRMED | ORDER_CANCELLED | PAYMENT_FAILED
        String status,        // PENDING | SENT | FAILED
        Integer attempts,
        String toEmail,
        String subject,
        String body,
        String lastError,
        Instant createdAt,
        Instant lastAttemptAt,
        Instant sentAt
) { }
