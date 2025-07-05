package com.fluxbank.wallet_service.application.dto;

import com.fluxbank.wallet_service.domain.enums.Currency;
import com.fluxbank.wallet_service.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResult(
        UUID transactionId,
        TransactionType type,
        Currency currency,
        BigDecimal amount,
        LocalDateTime transitionedAt
) {
}
