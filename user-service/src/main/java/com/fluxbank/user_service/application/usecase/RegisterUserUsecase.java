package com.fluxbank.user_service.application.usecase;

import com.fluxbank.user_service.interfaces.dto.CreateUserRequest;
import com.fluxbank.user_service.interfaces.dto.UserDeviceDto;

public interface RegisterUserUsecase {
    void register(CreateUserRequest request, UserDeviceDto deviceInfo);
}
