package com.fluxbank.notification_service.domain.factory;

import com.fluxbank.notification_service.domain.enums.NotificationEventType;
import com.fluxbank.notification_service.domain.enums.NotificationStatus;
import com.fluxbank.notification_service.domain.enums.NotificationTopic;
import com.fluxbank.notification_service.domain.enums.NotificationType;
import com.fluxbank.notification_service.domain.model.Notification;
import com.fluxbank.notification_service.interfaces.dto.PixkeyCreatedEventData;
import com.fluxbank.notification_service.interfaces.dto.TransactionNotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
public class NotificationFactory {

    public Notification createFromTransactionEvent(
            TransactionNotificationEvent event, 
            NotificationEventType eventType,
            UUID targetUserId,
            String targetUserEmail) {
        
        log.info("Creating notification for event: {} - user: {} - transaction: {}", 
            eventType, targetUserId, event.transactionId());
        
        LocalDateTime now = LocalDateTime.now();
        
        return Notification.builder()
                .id(UUID.randomUUID())
                .userId(targetUserId)
                .subject(targetUserEmail)
                
                .type(NotificationType.EMAIL)
                .topic(mapEventTypeToTopic(eventType))
                .status(NotificationStatus.PENDING)
                
                .title(generateNotificationTitle(eventType, event))
                .content("")
                
                .transactionId(event.transactionId())
                .transactionType(event.transactionType())
                .eventType(event.eventType())
                .amount(event.amount())
                .currency(event.currency())
                .description(event.description())
                .account(event.account())
                .transactionProcessedAt(event.processedAt())
                .failureReason(event.failureReason())

                .createdAt(now)
                .build();
    }

    public Notification createFromPixKeyCreatedEvent(
            PixkeyCreatedEventData event,
            UUID targetUserId) {

        NotificationEventType notificationType = NotificationEventType.PIX_KEY_CREATED;

        log.info("Creating notification for pix key created event: {} - user: {} - pixkey: {}",
                notificationType , targetUserId, event.key());

        LocalDateTime now = LocalDateTime.now();

        return Notification.builder()
                .id(UUID.randomUUID())
                .userId(targetUserId)
                .subject(event.userEmail())

                .type(NotificationType.EMAIL)
                .topic(NotificationTopic.PIX_KEY_CREATED)
                .status(NotificationStatus.PENDING)

                .title(generateNotificationTitle(notificationType, null))
                .content("")

                .transactionId(null)
                .transactionType(null)
                .eventType(null)
                .amount(null)
                .currency(null)
                .description(null)
                .account(event.key())
                .transactionProcessedAt(event.issuedAt())
                .failureReason(null)
                .createdAt(now)
                .build();
    }
    
    public Notification createNotificationForRetry(Notification original, String errorMessage) {

        return original.builder()
                .status(NotificationStatus.PENDING)
                .build();
    }
    
    public Notification markAsSent(Notification notification) {
        LocalDateTime now = LocalDateTime.now();
        
        return notification.builder()
                .status(NotificationStatus.SENT)
                .sentAt(now)
                .build();
    }
    
    public Notification markAsFailed(Notification notification, String errorMessage) {
        return notification.builder()
                .failureReason(errorMessage)
                .status(NotificationStatus.FAILED)
                .build();
    }
    
    private NotificationTopic mapEventTypeToTopic(NotificationEventType eventType) {
        return switch (eventType) {
            case PIX_SENT, PIX_SENT_FAILED -> NotificationTopic.PIX_SENT;
            case PIX_RECEIVED, PIX_RECEIVED_FAILED -> NotificationTopic.PIX_RECEIVED;
            case PIX_KEY_CREATED -> NotificationTopic.PIX_KEY_CREATED;
            case LIMIT_EXCEEDED -> NotificationTopic.LIMIT_EXCEED;
            default -> NotificationTopic.PIX_SENT;
        };
    }
    
    private String generateNotificationTitle(NotificationEventType eventType, TransactionNotificationEvent event) {
        return switch (eventType) {
            case PIX_SENT -> "PIX Enviado - " + event.currency() + " " + formatAmount(event.amount());
            case PIX_RECEIVED -> "PIX Recebido - " + event.currency() + " " + formatAmount(event.amount());
            case PIX_SENT_FAILED -> "PIX Falhou - " + event.currency() + " " + formatAmount(event.amount());
            case PIX_RECEIVED_FAILED -> "Falha no PIX Recebido";
            case PIX_KEY_CREATED -> "Chave PIX Criada com Sucesso";
            case LIMIT_EXCEEDED -> "Limite de Transações Excedido";
            case CARD_TRANSACTION_COMPLETED -> "Transação Cartão - " + event.currency() + " " + formatAmount(event.amount());
            case TRANSACTION_FAILED -> "Transação Falhou";
            default -> "Notificação FluxBank";
        };
    }
    
    private String formatAmount(java.math.BigDecimal amount) {
        if (amount == null) return "0,00";
        
        return String.format("%.2f", amount).replace(".", ",");
    }
}