package com.fluxbank.wallet_service.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record GetWalletLimitsRequest(
        @Schema(description = "Id da wallet referente")
        @NotNull
        String walletId
) {
}
