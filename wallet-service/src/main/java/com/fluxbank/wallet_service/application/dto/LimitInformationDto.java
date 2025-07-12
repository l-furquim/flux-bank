package com.fluxbank.wallet_service.application.dto;

import com.fluxbank.wallet_service.domain.enums.LimitStatus;
import com.fluxbank.wallet_service.domain.enums.LimitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LimitInformationDto(
        LimitType type,
        BigDecimal amount,
        BigDecimal used,
        LimitStatus status,
        LocalDateTime lastTimeUsed
) {
}
