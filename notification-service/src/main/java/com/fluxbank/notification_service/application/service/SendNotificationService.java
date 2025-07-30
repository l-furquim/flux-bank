package com.fluxbank.notification_service.application.service;

import com.fluxbank.notification_service.application.usecase.SendNotificationUsecase;
import com.fluxbank.notification_service.domain.enums.EventSource;
import com.fluxbank.notification_service.domain.enums.NotificationEventType;
import com.fluxbank.notification_service.domain.factory.NotificationFactory;
import com.fluxbank.notification_service.domain.model.Notification;
import com.fluxbank.notification_service.domain.repository.NotificationRepository;
import com.fluxbank.notification_service.interfaces.dto.PixkeyCreatedEventData;
import com.fluxbank.notification_service.interfaces.dto.TransactionNotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class SendNotificationService implements SendNotificationUsecase {

    private final NotificationRepository repository;
    private final NotificationEventRouter eventRouter;
    private final NotificationFactory notificationFactory;

    public SendNotificationService(
            NotificationRepository repository, 
            NotificationEventRouter eventRouter,
            NotificationFactory notificationFactory) {
        this.repository = repository;
        this.eventRouter = eventRouter;
        this.notificationFactory = notificationFactory;
    }

    @Override
    public void sendTransactionUsecase(TransactionNotificationEvent data) {
        try {
            log.info("Processing notification event: {} - {}", data.eventType(), data.transactionId());
            
            EventSource eventSource = EventSource.TRANSACTION_SERVICE;
            
            NotificationEventType eventType = NotificationEventType.fromEventTypeAndTransactionType(
                data.eventType(), 
                data.transactionType(), 
                data.status()
            );
            
            UUID targetUserId = determineTargetUserId(data);
            String targetUserEmail = determineTargetUserEmail(data);

            Notification notification = notificationFactory.createFromTransactionEvent(
                data, eventType, targetUserId,targetUserEmail
            );
            
            Notification savedNotification = repository.save(notification);

            try {
                eventRouter.routeTransactionEvent(data, eventSource);
                
                Notification sentNotification = notificationFactory.markAsSent(savedNotification);
                repository.save(sentNotification);
                
                log.info("Notification sent successfully: {}", savedNotification.getId());
                
            } catch (Exception sendException) {
                log.error("Failed to send notification: {}", savedNotification.getId(), sendException);
                
                Notification failedNotification = notificationFactory.markAsFailed(
                    savedNotification, sendException.getMessage()
                );
                repository.save(failedNotification);
                
                throw sendException;
            }

            log.info("Notification event processed successfully: {}", data.transactionId());
            
        } catch (Exception e) {
            log.error("Failed to process notification event: {}", data.transactionId(), e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }

    @Override
    public void sendPixkeyCreatedUsecase(PixkeyCreatedEventData data) {
        try {
            log.info("Processing pix key created notification event: {}", data.userEmail());

            EventSource source = EventSource.USER_SERVICE;

            Notification notification = notificationFactory.createFromPixKeyCreatedEvent(
                    data, UUID.fromString(data.userId())
            );

            Notification savedNotification = repository.save(notification);

            try {
                eventRouter.routePixKetCreatedEvent(data,source );

                Notification sentNotification = notificationFactory.markAsSent(savedNotification);
                repository.save(sentNotification);

                log.info("Pix key created notification sent successfully: {}", savedNotification.getId());
            } catch (Exception e) {
                log.error("Failed to send notification: {}", savedNotification.getId(), e);

                Notification failedNotification = notificationFactory.markAsFailed(
                        savedNotification, e.getMessage()
                );
                repository.save(failedNotification);

                throw e;
            }

        } catch (Exception e) {
            log.error("Failed to process notification event: {}", data.key(), e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }

    private UUID determineTargetUserId(TransactionNotificationEvent data) {
        if ("SENT".equals(data.eventType())) {
            return data.payerId();
        }
        if ("RECEIVED".equals(data.eventType())) {
            return data.payeeId();
        }
        return data.payerId();
    }

    private String determineTargetUserEmail(TransactionNotificationEvent data) {
        if ("SENT".equals(data.eventType())) {
            return data.payerEmail();
        }
        if ("RECEIVED".equals(data.eventType())) {
            return data.payeeEmail();
        }
        return data.payerEmail();
    }
}
