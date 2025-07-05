package com.fluxbank.user_service.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AuthUserRequest(

        @Schema(description = "Cpf do usuario referente")
        @NotBlank
        String cpf,

        @Schema(description = "Senha da conta")
        @NotBlank
        String password
) {
}
