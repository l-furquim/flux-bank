package com.fluxbank.user_service.application.usecase;

import com.fluxbank.user_service.interfaces.dto.ResolvePixKeyResponse;

public interface ResolvePixKeyUsecase {

    ResolvePixKeyResponse resolve(String keyValue);

}
