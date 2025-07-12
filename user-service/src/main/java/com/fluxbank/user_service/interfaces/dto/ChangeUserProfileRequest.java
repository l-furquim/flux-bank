package com.fluxbank.user_service.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;

public record ChangeUserProfileRequest(
        @Nullable
        @Schema(description = "Novo nome a ser utilizado")
        String fullName,

        @Nullable
        @Email
        @Schema(description = "Novo email a ser utilizado")
        String email,

        @Nullable
        @Schema(description = "Novo endereço a ser utilizado")
        String address
) {
}
