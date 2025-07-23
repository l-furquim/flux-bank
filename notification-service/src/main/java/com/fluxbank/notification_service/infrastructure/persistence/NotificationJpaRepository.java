package com.fluxbank.notification_service.infrastructure.persistence;

import com.fluxbank.notification_service.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationJpaRepository extends MongoRepository<NotificationEntity, UUID> {

    List<NotificationEntity> findByUserId(UUID userId);

    List<NotificationEntity> findByTransactionId(UUID transactionId);
}
