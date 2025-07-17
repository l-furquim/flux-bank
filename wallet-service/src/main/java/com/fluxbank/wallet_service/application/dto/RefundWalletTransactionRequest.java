package com.fluxbank.wallet_service.application.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


public record RefundWalletTransactionRequest(

        @Schema(description = "Id da transação de carteira referente")
        @NotBlank
        String walletTransactionId,

        @Schema(description = "Id do usuario que recebeu o deposito")
        @NotBlank
        String payeeId

) {
}
