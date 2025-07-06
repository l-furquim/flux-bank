package com.fluxbank.user_service.domain.service;

import com.fluxbank.user_service.interfaces.dto.UserTokenData;

public interface CacheService {

    void cacheToken(String token, UserTokenData tokenData);

}
