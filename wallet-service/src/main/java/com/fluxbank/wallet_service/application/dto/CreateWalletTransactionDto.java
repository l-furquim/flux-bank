package com.fluxbank.wallet_service.application.dto;

import com.fluxbank.wallet_service.domain.enums.TransactionType;
import com.fluxbank.wallet_service.domain.models.Wallet;

import java.math.BigDecimal;

public record CreateWalletTransactionDto(
            Wallet wallet,
            TransactionType transactionType,
            BigDecimal amount,
            BigDecimal balanceBefore,
            BigDecimal balanceAfter,
            String externalReference,
            String description,
            String metadata
) {
}
