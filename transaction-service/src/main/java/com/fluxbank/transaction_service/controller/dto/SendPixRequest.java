package com.fluxbank.transaction_service.controller.dto;

import com.fluxbank.transaction_service.model.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SendPixRequest(

        @Schema(description = "Dinheiro a ser enviado pelo pix")
        @NotNull
        BigDecimal amount,

        @Schema(description = "Chave pix do destinatário")
        @NotBlank
        String destineKey,

        @Schema(description = "Descrição da transação (opcional)")
        String description,

        @Schema(description = "Moeda da transação e ser realizada", example = "BRL, USD, EUR")
        @NotNull
        Currency currency

) {
}
