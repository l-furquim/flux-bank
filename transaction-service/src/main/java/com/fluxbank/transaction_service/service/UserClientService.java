package com.fluxbank.transaction_service.service;

import com.fluxbank.transaction_service.client.IUserClient;
import com.fluxbank.transaction_service.controller.dto.ResolvePixKeyResponse;
import com.fluxbank.transaction_service.model.exceptions.PixKeyNotFoundException;
import com.fluxbank.transaction_service.model.exceptions.ResolvePixKeyException;
import com.fluxbank.transaction_service.model.exceptions.UserClientUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class UserClientService {

    private final IUserClient client;

    public UserClientService(IUserClient client) {
        this.client = client;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackResolvePixKey")
    public UUID resolvePixKey(String keyValue) {
        ResponseEntity<ResolvePixKeyResponse> response = client.resolvePixKey(keyValue);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().userId();
        }

        throw new ResolvePixKeyException("Pix key inválida ou não encontrada");
    }

    private UUID fallbackResolvePixKey(String keyValue, Throwable throwable) {
        log.error("Exceção caiu no fallback '{}': {}", keyValue, throwable.getMessage());

        if (throwable instanceof FeignException.NotFound) {
            throw new PixKeyNotFoundException("Chave PIX não encontrada.");
        }

        throw new UserClientUnavailableException("Erro ao acessar serviço de usuário: " + throwable.getMessage());
    }
}

