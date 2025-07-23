package com.fluxbank.user_service.interfaces.dto;

import java.util.UUID;

public record GetUserDataResponse(
        UUID userId,
        String email,
        String fullName,
        String cpf
) {
}