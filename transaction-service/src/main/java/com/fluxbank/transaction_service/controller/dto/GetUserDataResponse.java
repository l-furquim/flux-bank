package com.fluxbank.transaction_service.controller.dto;

import java.util.UUID;

public record GetUserDataResponse(
        UUID userId,
        String email,
        String fullName,
        String cpf
) {
}