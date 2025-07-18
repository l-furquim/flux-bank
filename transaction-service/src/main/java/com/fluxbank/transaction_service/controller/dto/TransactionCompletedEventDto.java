package com.fluxbank.transaction_service.controller.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionCompletedEventDto(
        UUID id,
        String type,
        UUID transactionId,
        String transactionType,
        String status,
        BigDecimal amount,
        String currency,
        Long processingDurationMs,
        Instant timestamp,
        String sourceService
) {
}
