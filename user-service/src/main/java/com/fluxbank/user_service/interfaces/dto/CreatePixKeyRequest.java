package com.fluxbank.user_service.interfaces.dto;

import com.fluxbank.user_service.domain.enums.KeyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreatePixKeyRequest(
        @Schema(description = "Valor da chave a ser criada")
        String value,

        @NotNull
        @Schema(description = "Tipo da chave pix", example = "EMAIL, CPF, TEL, RANDOM")
        KeyType type
) {
}
