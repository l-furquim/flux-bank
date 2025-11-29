package com.fluxbank.transaction_service.controller.dto;

import com.fluxbank.transaction_service.model.enums.Currency;
import com.fluxbank.transaction_service.model.enums.ScheduleRecurrence;

import java.math.BigDecimal;

public record SchedulePixRequest(
        BigDecimal amount,
        Currency currency,
        String pixKey,
        String description,
        String scheduledDate,
        String scheduledTime,
        ScheduleRecurrence recurrence
) {
}
