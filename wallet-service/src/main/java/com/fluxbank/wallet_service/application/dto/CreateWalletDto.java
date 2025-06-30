package com.fluxbank.wallet_service.application.dto;

import com.fluxbank.wallet_service.domain.enums.Currency;

import java.util.UUID;

public record CreateWalletDto(
        UUID userId,
        Currency currency
){}
