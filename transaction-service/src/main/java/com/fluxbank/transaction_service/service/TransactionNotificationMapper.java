package com.fluxbank.transaction_service.service;

import com.fluxbank.transaction_service.controller.dto.TransactionNotificationDto;
import com.fluxbank.transaction_service.controller.dto.GetUserDataResponse;
import com.fluxbank.transaction_service.model.CardTransaction;
import com.fluxbank.transaction_service.model.PixTransaction;
import com.fluxbank.transaction_service.model.Transaction;
import com.fluxbank.transaction_service.service.UserClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class TransactionNotificationMapper {
    
    private final NotificationEventTypeHelper helper;
    private final UserClientService userClientService;
    
    public TransactionNotificationMapper(NotificationEventTypeHelper helper, UserClientService userClientService) {
        this.helper = helper;
        this.userClientService = userClientService;
    }

    public List<TransactionNotificationDto> mapTransactionToNotifications(Transaction transaction) {
        if (!helper.hasValidDataForNotification(transaction)) {
            log.warn("Transaction {} lacks required data for notifications", transaction.getId());
            return List.of();
        }
        
        if (!helper.shouldNotify(transaction)) {
            log.debug("Transaction {} with status {} does not require notifications", 
                transaction.getId(), transaction.getStatus());
            return List.of();
        }
        List<TransactionNotificationDto> notifications = new ArrayList<>();
        
        String account = extractAccountInfo(transaction);
        String failureReason = determineFailureReason(transaction);
        
        GetUserDataResponse payerData = getUserData(transaction.getPayerId().toString());
        GetUserDataResponse payeeData = getUserData(transaction.getPayeeId().toString());
        
        if (transaction instanceof PixTransaction) {
            notifications.add(createNotification(
                transaction, "SENT", account, failureReason, payerData, payeeData
            ));
            
            notifications.add(createNotification(
                transaction, "RECEIVED", account, failureReason, payerData, payeeData
            ));
            
            log.info("Created PIX notifications for transaction {}: SENT and RECEIVED", transaction.getId());
            
        } else if (transaction instanceof CardTransaction) {
            String eventType = determineCardEventType((CardTransaction) transaction);
            
            notifications.add(createNotification(
                transaction, eventType, account, failureReason, payerData, payeeData
            ));
            
            log.info("Created Card notification for transaction {}: {}", transaction.getId(), eventType);
        } else {
            notifications.add(createNotification(
                transaction, "COMPLETED", account, failureReason, payerData, payeeData
            ));
            
            log.info("Created generic notification for transaction {}: COMPLETED", transaction.getId());
        }
        
        return notifications;
    }
    
    public TransactionNotificationDto createSingleNotification(Transaction transaction, String eventType) {
        String account = extractAccountInfo(transaction);
        String failureReason = determineFailureReason(transaction);
        
        GetUserDataResponse payerData = getUserData(transaction.getPayerId().toString());
        GetUserDataResponse payeeData = getUserData(transaction.getPayeeId().toString());
        
        return createNotification(transaction, eventType, account, failureReason, payerData, payeeData);
    }
    
    private TransactionNotificationDto createNotification(Transaction transaction, String eventType, 
                                                         String account, String failureReason, 
                                                         GetUserDataResponse payerData, 
                                                         GetUserDataResponse payeeData) {
        return new TransactionNotificationDto(
            transaction.getId(),
            eventType,
            transaction.getTransactionType().toString(),
            transaction.getStatus().toString(),
            transaction.getPayerId(),
            transaction.getPayeeId(),
            transaction.getAmount(),
            transaction.getCurrency().toString(),
            transaction.getDescription(),
            account,
            transaction.getProcessedAt(),
            failureReason,
            payerData.email(),
            payerData.fullName(),
            payerData.cpf(),
            payeeData.email(),
            payeeData.fullName(),
            payeeData.cpf()
        );
    }
    
    private String extractAccountInfo(Transaction transaction) {
        if (transaction instanceof PixTransaction pix) {
            return pix.getKey();
        } else if (transaction instanceof CardTransaction card) {
            return card.getLastFourDigits();
        }
        return "";
    }
    
    private String determineFailureReason(Transaction transaction) {
        switch (transaction.getStatus()) {
            case FAILED -> {
                // TODO: Implementar lógica baseada no contexto da falha
                return "Transaction processing failed";
            }
            case CANCELED -> {
                return "Transaction was canceled";
            }
            default -> {
                return null;
            }
        }
    }
    
    private String determineCardEventType(CardTransaction card) {
        return switch (card.getCardType()) {
            case CREDIT -> "CREDIT_TRANSACTION";
            case DEBIT -> "DEBIT_TRANSACTION"; 
            default -> "CARD_TRANSACTION";
        };
    }
    
    public boolean shouldNotifyPayer(Transaction transaction, String eventType) {
        return "SENT".equals(eventType) ||
               "CREDIT_TRANSACTION".equals(eventType) || 
               "DEBIT_TRANSACTION".equals(eventType) ||
               "COMPLETED".equals(eventType);
    }
    
    public boolean shouldNotifyPayee(Transaction transaction, String eventType) {
        return "RECEIVED".equals(eventType);
    }
    
    public UUID getNotificationTargetUserId(Transaction transaction, String eventType) {
        if (shouldNotifyPayer(transaction, eventType)) {
            return transaction.getPayerId();
        } else if (shouldNotifyPayee(transaction, eventType)) {
            return transaction.getPayeeId();
        }
        return transaction.getPayerId();
    }
    
    private GetUserDataResponse getUserData(String userId) {
        try {
            return userClientService.getUserData(userId);
        } catch (Exception e) {
            log.warn("Failed to get user data for {}: {}", userId, e.getMessage());
            return new GetUserDataResponse(UUID.fromString(userId), "", "", "");
        }
    }
}