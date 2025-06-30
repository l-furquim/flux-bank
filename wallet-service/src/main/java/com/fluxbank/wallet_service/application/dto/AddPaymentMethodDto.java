package com.fluxbank.wallet_service.application.dto;

import com.fluxbank.wallet_service.domain.enums.MethodType;

import java.time.LocalDateTime;

public record AddPaymentMethodDto(
        String walletId,
        MethodType methodType,
        String displayName,
        String lastFourDigits,
        LocalDateTime expireDate,
        String metadata,
        Boolean setAsDefault
) {
}
