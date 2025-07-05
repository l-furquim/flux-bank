package com.fluxbank.user_service.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateUserRequest(

        @Schema(description = "Nome completo do usuario")
        @NotBlank
        String fullName,

        @Schema(description = "Cnpj do usuário")
        @NotBlank
        String cpf,

        @Schema(description = "Email do usuario")
        @Email
        @NotBlank
        String email,

        @Schema(description = "Senha do usuario")
        @NotBlank
        String password,

        @Schema(description = "Data de aniversario do usuario")
        @NotBlank
        LocalDate birthDate,

        @Schema(description = "Endereço do usuario")
        @NotBlank
        String address

  ) {
}
