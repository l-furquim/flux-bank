package com.fluxbank.user_service.application.usecase;

import com.fluxbank.user_service.interfaces.dto.GetUserPixKeysResponse;

public interface GetUserPixKeysUsecase {

    GetUserPixKeysResponse get(String userId);

}
