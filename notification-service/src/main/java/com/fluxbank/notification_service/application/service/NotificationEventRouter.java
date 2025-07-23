package com.fluxbank.notification_service.application.service;

import com.fluxbank.notification_service.domain.enums.EventSource;
import com.fluxbank.notification_service.domain.enums.NotificationEventType;
import com.fluxbank.notification_service.domain.service.MailService;
import com.fluxbank.notification_service.interfaces.dto.TransactionNotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationEventRouter {
    
    private final MailService mailService;
    
    public NotificationEventRouter(MailService mailService) {
        this.mailService = mailService;
    }
    
    public void routeEvent(TransactionNotificationEvent event, EventSource eventSource) {
        log.info("Routing notification event: {} from source: {}", event.eventType(), eventSource);
        
        NotificationEventType eventType = NotificationEventType.fromEventTypeAndTransactionType(
            event.eventType(), 
            event.transactionType(), 
            event.status()
        );
        
        log.info("Determined event type: {} -> template: {}", eventType, eventType.getTemplateName());
        
        switch (eventType) {
            case PIX_SENT, PIX_SENT_FAILED -> {
                mailService.sendPixSent(event);
                log.info("PIX sent notification processed for transaction: {}", event.transactionId());
            }
            case PIX_RECEIVED, PIX_RECEIVED_FAILED -> {
                mailService.sendPixReceived(event);
                log.info("PIX received notification processed for transaction: {}", event.transactionId());
            }
            case PIX_KEY_CREATED -> {
                mailService.sendPixKeyCreated();
                log.info("PIX key created notification processed");
            }
            case LIMIT_EXCEEDED -> {
                mailService.sendLimitExceeded();
                log.info("Limit exceeded notification processed");
            }
            case CARD_TRANSACTION_COMPLETED, DEPOSIT_COMPLETED, WITHDRAWAL_COMPLETED -> {
                log.info("Card/Deposit/Withdrawal transaction notification processed for: {}", event.transactionId());
            }
            case TRANSACTION_FAILED -> {
                log.info("Generic transaction failed notification processed for: {}", event.transactionId());
            }
            case UNKNOWN -> {
                log.warn("Unknown event type received: {} - {}", event.eventType(), event.transactionType());
            }
        }
    }
    
    public NotificationEventType determineEventType(TransactionNotificationEvent event) {
        return NotificationEventType.fromEventTypeAndTransactionType(
            event.eventType(), 
            event.transactionType(), 
            event.status()
        );
    }
}