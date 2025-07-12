package com.fluxbank.user_service.interfaces.dto;

import com.fluxbank.user_service.domain.enums.KeyType;

public record PixKeyInfoDto(
        KeyType type,
        String value
) {
}
