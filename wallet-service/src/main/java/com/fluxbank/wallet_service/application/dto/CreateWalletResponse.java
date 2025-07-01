package com.fluxbank.wallet_service.application.dto;

import com.fluxbank.wallet_service.domain.models.Wallet;

public record CreateWalletResponse(
        Wallet wallet
) {
}
