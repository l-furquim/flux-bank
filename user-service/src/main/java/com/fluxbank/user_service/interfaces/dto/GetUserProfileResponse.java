package com.fluxbank.user_service.interfaces.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record GetUserProfileResponse(
        String cpf,
        String fullName,
        String email,
        LocalDate birthDate,
        String address,
        LocalDateTime createdAt
) {
}
