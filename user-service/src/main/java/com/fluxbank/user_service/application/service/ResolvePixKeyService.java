package com.fluxbank.user_service.application.service;

import com.fluxbank.user_service.application.usecase.ResolvePixKeyUsecase;
import com.fluxbank.user_service.domain.exceptions.KeyNotFoundException;
import com.fluxbank.user_service.domain.model.PixKey;
import com.fluxbank.user_service.domain.repository.PixKeyRepository;
import com.fluxbank.user_service.interfaces.dto.ResolvePixKeyResponse;
import org.springframework.stereotype.Service;

@Service
public class ResolvePixKeyService implements ResolvePixKeyUsecase {

    private final PixKeyRepository repository;

    public ResolvePixKeyService(PixKeyRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResolvePixKeyResponse resolve(String keyValue) {

        PixKey key = repository.findByValue(keyValue);

        if(key == null) {
            throw new KeyNotFoundException("Key for resolve not found.");
        }

        return new ResolvePixKeyResponse(
                key.getOwnerId()
        );
    }
}
