package com.fluxbank.user_service.application.usecase;

import com.fluxbank.user_service.interfaces.dto.AuthUserRequest;
import com.fluxbank.user_service.interfaces.dto.AuthUserResponse;


public interface AuthUserUsecase {

    AuthUserResponse auth(AuthUserRequest request, String userAgent);

}
