package com.fluxbank.notification_service.infrastructure.persistence.impl;

import com.fluxbank.notification_service.domain.model.Notification;
import com.fluxbank.notification_service.domain.repository.NotificationRepository;
import com.fluxbank.notification_service.infrastructure.persistence.NotificationJpaRepository;
import com.fluxbank.notification_service.infrastructure.persistence.entity.NotificationEntity;
import com.fluxbank.notification_service.infrastructure.persistence.mapper.NotificationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NotificationJpaImpl implements NotificationRepository {

    private final NotificationMapper mapper;
    private final NotificationJpaRepository repository;

    public NotificationJpaImpl(NotificationMapper mapper, NotificationJpaRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public Notification save(Notification notification) {
        try {
            log.info("Saving notification for user {} with transaction {}", 
                notification.getUserId(), notification.getTransactionId());
            
            NotificationEntity entityToBePersisted = mapper.toEntity(notification);
            NotificationEntity savedEntity = repository.save(entityToBePersisted);
            
            log.info("Notification saved successfully with ID: {}", savedEntity.getId());
            return mapper.toDomain(savedEntity);
            
        } catch (Exception e) {
            log.error("Failed to save notification for user {} with transaction {}: {}", 
                notification.getUserId(), notification.getTransactionId(), e.getMessage(), e);
            throw new RuntimeException("Failed to save notification", e);
        }
    }


    public Notification update(Notification notification) {
        try {
            log.info("Updating notification with ID: {}", notification.getId());
            
            Optional<NotificationEntity> existingEntity = repository.findById(notification.getId());
            
            if (existingEntity.isPresent()) {
                NotificationEntity updated = mapper.updateEntity(existingEntity.get(), notification);
                NotificationEntity savedEntity = repository.save(updated);
                
                log.info("Notification updated successfully with ID: {}", savedEntity.getId());
                return mapper.toDomain(savedEntity);
            } else {
                log.warn("Notification not found for update with ID: {}", notification.getId());
                return save(notification); // Criar novo se não existir
            }
            
        } catch (Exception e) {
            log.error("Failed to update notification with ID {}: {}", notification.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to update notification", e);
        }
    }
}
