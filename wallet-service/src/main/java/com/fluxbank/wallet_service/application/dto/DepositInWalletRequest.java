package com.fluxbank.wallet_service.application.dto;

import com.fluxbank.wallet_service.domain.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositInWalletRequest(
        @Schema(description = "Quantidade de dinheiro a ser depositado" ,example = "19.10")
        @NotNull BigDecimal amount,

        @Schema(description = "Id da carteira referente", example = "qweoiqwjeoiqwjewq")
        @NotNull String walletId,

        @Schema(description = "Tipo de deposito a ser realizado", example = "CREDIT, DEBIT, PIX, REFUND")
        @NotNull TransactionType type,

        @NotNull String metadata,

        @Schema(description= "Descrição do deposito caso seja necessário")
        String description
) {
}
