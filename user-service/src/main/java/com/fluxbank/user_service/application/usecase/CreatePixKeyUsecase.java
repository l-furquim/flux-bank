package com.fluxbank.user_service.application.usecase;

import com.fluxbank.user_service.interfaces.dto.CreatePixKeyRequest;

public interface CreatePixKeyUsecase {

    void create(CreatePixKeyRequest request, String userId);

}
