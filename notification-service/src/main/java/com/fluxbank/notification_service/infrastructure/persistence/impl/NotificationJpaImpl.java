package com.fluxbank.notification_service.infrastructure.persistence.impl;

import com.fluxbank.notification_service.domain.model.Notification;
import com.fluxbank.notification_service.domain.repository.NotificationRepository;
import com.fluxbank.notification_service.infrastructure.persistence.NotificationJpaRepository;
import com.fluxbank.notification_service.infrastructure.persistence.entity.NotificationEntity;
import com.fluxbank.notification_service.infrastructure.persistence.mapper.NotificationMapper;
import org.springframework.stereotype.Component;

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
        NotificationEntity entityToBePersisted = mapper.toEntity(notification);

        return mapper.toDomain(repository.save(entityToBePersisted));
    }
}
