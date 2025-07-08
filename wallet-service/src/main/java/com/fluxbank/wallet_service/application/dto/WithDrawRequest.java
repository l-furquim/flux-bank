package com.fluxbank.wallet_service.application.dto;

import com.fluxbank.wallet_service.domain.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record WithDrawRequest(

        @Schema(description = "Id da carteira referente")
        @NotBlank
        String walletId,

        @Schema(description = "Quantidade a ser sacada")
        @NotNull
        BigDecimal amount,

        @Schema(description = "Id da transação referente")
        @NotNull
        UUID transactionId,

        @Schema(description = "Tipo da transação de saque")
        @NotNull
        TransactionType type,

        @Schema(description = "Metadata do provedor")
        @NotNull
        String metadata

) {
}
