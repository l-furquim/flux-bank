package com.fluxbank.user_service.application.usecase;

import com.fluxbank.user_service.interfaces.dto.ChangeUserProfileRequest;

public interface ChangeProfileUsecase {

    void change(ChangeUserProfileRequest request, String userId);

}
