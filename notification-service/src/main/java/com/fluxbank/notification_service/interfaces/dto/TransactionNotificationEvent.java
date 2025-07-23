package com.fluxbank.notification_service.interfaces.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionNotificationEvent(
    UUID transactionId,
    String eventType,
    String transactionType,
    String status,
    UUID payerId,
    UUID payeeId, 
    BigDecimal amount,
    String currency,
    String description,
    String account,
    LocalDateTime processedAt,
    String failureReason,
    String payerEmail,
    String payerName,
    String payerCpf,
    String payeeEmail,
    String payeeName,
    String payeeCpf
) {
}