package com.fluxbank.wallet_service.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WalletUpdatedEventDto(
        UUID transactionId,
        String userId,
        String transactionType,
        String status,
        BigDecimal amount,
        String currency,
        long processingDurationMs,
        Instant timestamp,
        String sourceService
) {
}
