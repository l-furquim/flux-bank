package com.fluxbank.transaction_service.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SendPixResponse(
        UUID transactionId,
        String message,
        LocalDateTime issuedAt
) {
}
