package com.fluxbank.wallet_service.application.dto;

import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.domain.enums.TransactionType;
import com.fluxbank.wallet_service.domain.models.Wallet;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public record CreateWalletTransactionDto(
            Wallet wallet,
            UUID transactionId,
            TransactionType transactionType,
            BigDecimal amount,
            String description,
            String metadata,
            Optional<TransactionStatus> status
) {
}
