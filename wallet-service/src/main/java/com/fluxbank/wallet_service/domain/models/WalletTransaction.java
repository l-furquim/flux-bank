package com.fluxbank.wallet_service.domain.models;

import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.domain.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class WalletTransaction {

    private UUID id;

    private Wallet wallet;

    private UUID transactionId;

    private TransactionType transactionType;

    private BigDecimal amount;

    private BigDecimal balanceBefore;

    private BigDecimal balanceAfter;

    private String description;

    private TransactionStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String metadata;

    public boolean isCompleted() {
        return status.equals(TransactionStatus.COMPLETED);
    }

    public boolean isCredit() {
        return transactionType.equals(TransactionType.CREDIT) ||
                transactionType.equals(TransactionType.REFUND);
    }

    public boolean isDebit() {
        return transactionType.equals(TransactionType.DEBIT);
    }

}
