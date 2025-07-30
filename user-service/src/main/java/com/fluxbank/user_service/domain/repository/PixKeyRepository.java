package com.fluxbank.user_service.domain.repository;

import com.fluxbank.user_service.domain.model.PixKey;
import com.fluxbank.user_service.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface PixKeyRepository {

    PixKey createPixKey(PixKey pixkey);
    List<PixKey> findByUserId(UUID userId);
    void updatePixKey(PixKey pixKey);
    void deletePixKey(UUID pixKeyId);
    PixKey findCpfKeyByUser(User user);
    PixKey findByValue(String value);

}
