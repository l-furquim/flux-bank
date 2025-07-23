package com.fluxbank.notification_service.infrastructure.persistence.mapper;

import com.fluxbank.notification_service.domain.model.Notification;
import com.fluxbank.notification_service.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class NotificationMapper {

    public Notification toDomain(NotificationEntity entity) {
        if (entity == null) return null;
        
        return Notification.builder()
                .id(entity.getId())
                .subject(entity.getSubject())
                .userId(entity.getUserId())
                .type(entity.getType())
                .topic(entity.getTopic())
                .status(entity.getStatus())
                .title(entity.getTitle())
                .content(entity.getContent())
                .sentAt(entity.getSentAt())
                .transactionId(entity.getTransactionId())
                .transactionType(entity.getTransactionType())
                .eventType(entity.getEventType())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .description(entity.getDescription())
                .account(entity.getAccount())
                .transactionProcessedAt(entity.getTransactionProcessedAt())
                .failureReason(entity.getFailureReason())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public NotificationEntity toEntity(Notification notification) {
        if (notification == null) return null;
        
        LocalDateTime now = LocalDateTime.now();
        
        return NotificationEntity.builder()
                .id(notification.getId() != null ? notification.getId() : UUID.randomUUID())
                .subject(notification.getSubject())
                .userId(notification.getUserId())
                .type(notification.getType())
                .topic(notification.getTopic())
                .status(notification.getStatus())
                .title(notification.getTitle())
                .content(notification.getContent())
                .sentAt(notification.getSentAt())
                .transactionId(notification.getTransactionId())
                .transactionType(notification.getTransactionType())
                .eventType(notification.getEventType())
                .amount(notification.getAmount())
                .currency(notification.getCurrency())
                .description(notification.getDescription())
                .account(notification.getAccount())
                .transactionProcessedAt(notification.getTransactionProcessedAt())
                .failureReason(notification.getFailureReason())
                .createdAt(notification.getCreatedAt() != null ? notification.getCreatedAt() : now)
                .build();
    }

    public NotificationEntity updateEntity(NotificationEntity entity, Notification notification) {
        if (entity == null || notification == null) return entity;
        
        entity.setStatus(notification.getStatus());
        entity.setSentAt(notification.getSentAt());
        
        return entity;
    }

}
