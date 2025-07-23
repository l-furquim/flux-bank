package com.fluxbank.transaction_service.service;

import com.fluxbank.transaction_service.model.Transaction;
import com.fluxbank.transaction_service.model.enums.TransactionStatus;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventTypeHelper {

    public boolean shouldNotify(Transaction transaction) {
        return transaction.getStatus() == TransactionStatus.COMPLETED ||
               transaction.getStatus() == TransactionStatus.FAILED;
    }

    public boolean shouldCreateMultipleNotifications(Transaction transaction) {
        return "PIX".equals(transaction.getTransactionType().toString()) &&
               transaction.getPayerId() != null && 
               transaction.getPayeeId() != null &&
               !transaction.getPayerId().equals(transaction.getPayeeId());
    }

    public boolean hasValidDataForNotification(Transaction transaction) {
        return transaction.getId() != null &&
               transaction.getAmount() != null &&
               transaction.getProcessedAt() != null &&
               (transaction.getPayerId() != null || transaction.getPayeeId() != null);
    }

    public String getNotificationDescription(Transaction transaction) {
        return switch (transaction.getStatus()) {
            case COMPLETED -> "Transaction completed successfully";
            case FAILED -> "Transaction failed to process";
            case CANCELED -> "Transaction was canceled";
            default -> "Transaction status updated";
        };
    }
}