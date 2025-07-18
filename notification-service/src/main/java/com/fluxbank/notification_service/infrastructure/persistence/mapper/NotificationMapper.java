package com.fluxbank.notification_service.infrastructure.persistence.mapper;

import com.fluxbank.notification_service.domain.model.Notification;
import com.fluxbank.notification_service.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class NotificationMapper {

    public Notification toDomain(NotificationEntity notification) {
        return Notification.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .topic(notification.getTopic())
                .type(notification.getType())
                .sentAt(notification.getSentAt())
                .content(notification.getContent())
                .subject(notification.getSubject())
                .userId(notification.getUserId())
                .status(notification.getStatus())
                .build();
    }

    public NotificationEntity toEntity(Notification notification) {
        return NotificationEntity.builder()
                .id(UUID.randomUUID())
                .title(notification.getTitle())
                .topic(notification.getTopic())
                .type(notification.getType())
                .sentAt(LocalDateTime.now())
                .content(notification.getContent())
                .subject(notification.getSubject())
                .userId(notification.getUserId())
                .status(notification.getStatus())
                .build();
    }

}
