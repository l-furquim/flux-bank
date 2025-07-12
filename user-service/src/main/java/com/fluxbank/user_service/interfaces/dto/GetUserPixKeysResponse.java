package com.fluxbank.user_service.interfaces.dto;

import java.util.List;

public record GetUserPixKeysResponse(
        List<PixKeyInfoDto> keys
) {
}
