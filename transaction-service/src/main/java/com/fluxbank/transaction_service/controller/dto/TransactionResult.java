package com.fluxbank.transaction_service.controller.dto;

import com.fluxbank.transaction_service.model.enums.Currency;
import com.fluxbank.transaction_service.model.enums.TransactionType;

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
