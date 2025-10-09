package com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.entity.JpaNotificationEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.notifications.infrastructure.web.dto.response.NotificationLogResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationMapper {

    public NotificationLogResponse toResponse(JpaNotificationEntity e) {
        if (e == null) return null;
        return new NotificationLogResponse(
                e.getId(),
                e.getTenantId(),
                e.getOrderId(),
                e.getType() != null ? e.getType().name() : null,
                e.getStatus() != null ? e.getStatus().name() : null,
                e.getAttempts(),
                e.getToEmail(),
                e.getSubject(),
                e.getBody(),
                e.getLastError(),
                e.getCreatedAt(),
                e.getLastAttemptAt(),
                e.getSentAt()
        );
    }

    public List<NotificationLogResponse> toResponses(List<JpaNotificationEntity> list) {
        return list == null ? List.of() : list.stream().map(this::toResponse).toList();
    }
}
