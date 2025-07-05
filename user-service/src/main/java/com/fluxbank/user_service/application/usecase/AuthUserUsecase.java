package com.fluxbank.user_service.application.usecase;

import com.fluxbank.user_service.interfaces.dto.AuthUserRequest;

public interface AuthUserUsecase {

    void auth(AuthUserRequest request);

}
