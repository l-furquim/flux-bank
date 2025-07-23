package com.fluxbank.notification_service.application.service;

import com.fluxbank.notification_service.application.usecase.SendNotificationUsecase;
import com.fluxbank.notification_service.domain.enums.EventSource;
import com.fluxbank.notification_service.domain.enums.NotificationEventType;
import com.fluxbank.notification_service.domain.factory.NotificationFactory;
import com.fluxbank.notification_service.domain.model.Notification;
import com.fluxbank.notification_service.domain.repository.NotificationRepository;
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
    public void send(TransactionNotificationEvent data) {
        try {
            log.info("Processing notification event: {} - {}", data.eventType(), data.transactionId());
            
            EventSource eventSource = EventSource.TRANSACTION_SERVICE;
            
            NotificationEventType eventType = NotificationEventType.fromEventTypeAndTransactionType(
                data.eventType(), 
                data.transactionType(), 
                data.status()
            );
            
            UUID targetUserId = determineTargetUser(data);
            
            Notification notification = notificationFactory.createFromTransactionEvent(
                data, eventType, targetUserId
            );
            
            Notification savedNotification = repository.save(notification);

            try {
                eventRouter.routeEvent(data, eventSource);
                
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
    
    private UUID determineTargetUser(TransactionNotificationEvent data) {
        if ("SENT".equals(data.eventType())) {
            return data.payerId();
        }
        else if ("RECEIVED".equals(data.eventType())) {
            return data.payeeId();
        }
        return data.payerId();
    }
}
