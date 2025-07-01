package com.fluxbank.wallet_service.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
public record CreateWalletRequest(
      @Schema(description = "Código da moeda da carteira", example = "BRL")
      @NotBlank String currency
){}
