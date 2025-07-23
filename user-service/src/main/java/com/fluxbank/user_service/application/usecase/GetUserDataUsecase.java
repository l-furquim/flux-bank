package com.fluxbank.user_service.application.usecase;

import com.fluxbank.user_service.interfaces.dto.GetUserDataResponse;

public interface GetUserDataUsecase {

    GetUserDataResponse get(String userId);
}