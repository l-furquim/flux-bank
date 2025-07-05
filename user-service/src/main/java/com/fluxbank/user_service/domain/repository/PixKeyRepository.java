package com.fluxbank.user_service.domain.repository;

import com.fluxbank.user_service.domain.model.PixKey;

import java.util.List;
import java.util.UUID;

public interface PixKeyRepository {

    void createPixKey(PixKey pixkey);
    List<PixKey> findByUserId(UUID userId);
    void updatePixKey(PixKey pixKey);
    void deletePixKey(UUID pixKeyId);


}
