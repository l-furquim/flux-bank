package com.fluxbank.user_service.domain.model;

import com.fluxbank.user_service.domain.enums.KeyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PixKey {

    private UUID id;

    private UUID ownerId;

    private KeyType type;

    private LocalDateTime issuedAt;

    private String value\;

}
