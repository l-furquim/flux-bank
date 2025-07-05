package com.fluxbank.user_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private UUID id;

    private String cpf;

    private String fullName;

    private String email;

    private String password;

    private LocalDate birthDate;

    private String address;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
