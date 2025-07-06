package com.fluxbank.user_service.interfaces.dto;

public record AuthUserResponse(
        String token,
        UserTokenData tokenData
) {
}
