package com.fluxbank.user_service.application.usecase;

import com.fluxbank.user_service.interfaces.dto.GetUserProfileResponse;

public interface GetProfileUsecase {

    GetUserProfileResponse get(String userId);

}
