package com.fluxbank.wallet_service.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record GetWalletBalanceRequest(
        @Schema(description = "Id da carteira referente")
        @NotBlank
        String walletId
) {
}
