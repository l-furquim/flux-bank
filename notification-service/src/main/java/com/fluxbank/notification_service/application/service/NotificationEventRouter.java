package com.fluxbank.notification_service.application.service;

import com.fluxbank.notification_service.domain.enums.EventSource;
import com.fluxbank.notification_service.domain.enums.NotificationEventType;
import com.fluxbank.notification_service.domain.service.MailService;
import com.fluxbank.notification_service.interfaces.dto.PixkeyCreatedEventData;
import com.fluxbank.notification_service.interfaces.dto.TransactionNotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Slf4j
@Component
public class NotificationEventRouter {
    
    private final MailService mailService;
    
    public NotificationEventRouter(MailService mailService) {
        this.mailService = mailService;
    }
    
    public void routeTransactionEvent(TransactionNotificationEvent event, EventSource eventSource) {
        log.info("Routing notification event: {} from source: {}", event.eventType(), eventSource);
        
        NotificationEventType eventType = NotificationEventType.fromEventTypeAndTransactionType(
            event.eventType(), 
            event.transactionType(), 
            event.status()
        );
        
        log.info("Determined event type: {} -> template: {}", eventType, eventType.getTemplateName());
        
        switch (eventType) {
            case PIX_SENT_FAILED -> {
                mailService.sendPixSentFailed(event, formatCurrencySimple(event.amount(), event.currency()));
                log.info("PIX failed for sent notification processed for transaction: {}", event.transactionId());
            }
            case PIX_SENT -> {
                mailService.sendPixSent(event, formatCurrencySimple(event.amount(), event.currency()));
                log.info("PIX sent notification processed for transaction: {}", event.transactionId());
            }
            case PIX_RECEIVED, PIX_RECEIVED_FAILED -> {
                mailService.sendPixReceived(event, formatCurrencySimple(event.amount(), event.currency()));
                log.info("PIX received notification processed for transaction: {}", event.transactionId());
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

    public void routePixKetCreatedEvent(PixkeyCreatedEventData data, EventSource eventSource){
        log.info("Routing notification event: {} from source: {}", data, eventSource);

        mailService.sendPixKeyCreated(data);
        log.info("PIX key created notification processed");
    }

    private String formatCurrencySimple(BigDecimal value, String currency) {
        if (value == null) return "0,00";

        Locale locale = switch (currency) {
            case "BR" -> new Locale("pt", "BR");
            case "USD" -> Locale.US;
            case "EUR" -> Locale.GERMANY;
            default -> Locale.getDefault();
        };

        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        return formatter.format(value);
    }
}