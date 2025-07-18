package com.fluxbank.transaction_service.controller.dto;

import com.fluxbank.transaction_service.model.enums.Currency;
import com.fluxbank.transaction_service.model.enums.TransactionDirection;
import com.fluxbank.transaction_service.model.enums.TransactionStatus;
import com.fluxbank.transaction_service.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionInfoDto(
        BigDecimal amount,
        Currency currency,
        TransactionStatus status,
        TransactionDirection direction,
        String description,
        TransactionType type,
        LocalDateTime processedAt
) {
}
