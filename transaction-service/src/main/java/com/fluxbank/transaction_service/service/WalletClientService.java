package com.fluxbank.transaction_service.service;

import com.fluxbank.transaction_service.client.IWalletClient;
import com.fluxbank.transaction_service.controller.dto.DepositInWalletRequest;
import com.fluxbank.transaction_service.controller.dto.DepositInWalletResponse;
import com.fluxbank.transaction_service.controller.dto.WithDrawRequest;
import com.fluxbank.transaction_service.controller.dto.WithDrawResponse;
import com.fluxbank.transaction_service.model.exceptions.InvalidDepositException;
import com.fluxbank.transaction_service.model.exceptions.InvalidWithDrawException;
import com.fluxbank.transaction_service.model.exceptions.WalletClientUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class WalletClientService {

    private final IWalletClient walletClient;

    public WalletClientService(IWalletClient walletClient) {
        this.walletClient = walletClient;
    }

    @CircuitBreaker(name = "walletService", fallbackMethod = "fallbackWithDrawWallet")
    public WithDrawResponse withDrawWallet(WithDrawRequest request, UUID userId) {
        ResponseEntity<WithDrawResponse> response = walletClient.withdraw(request, userId.toString());

        if (response == null) {
            throw new RuntimeException("Aaa");
        }

        return response.getBody();
    }


    private WithDrawResponse fallbackWithDrawWallet(WithDrawRequest request, UUID userId, Throwable throwable) {
        log.error("Exceção caiu no fallback for withdraw (userId={}): {}", userId, throwable.getMessage());

        if (throwable instanceof FeignException.BadRequest
                || throwable instanceof FeignException.Forbidden
                || throwable instanceof FeignException.UnprocessableEntity
                || throwable instanceof FeignException.NotFound) {
            throw new InvalidWithDrawException(throwable.getMessage());
        }

        throw new WalletClientUnavailableException("Erro ao acessar serviço de carteira: " + throwable.getMessage());
    }

    @CircuitBreaker(name = "walletService", fallbackMethod = "fallbackDepositWallet")
    public DepositInWalletResponse depositWallet(DepositInWalletRequest request) {
        ResponseEntity<DepositInWalletResponse> response = walletClient.deposit(request);

        if (response == null) {
            throw new RuntimeException("Aaa");
        }

        return response.getBody();
    }

    private DepositInWalletResponse fallbackDepositWallet(DepositInWalletRequest request, Throwable throwable) {
        log.error("Exceção caiu no fallback for deposit (transaction={}): {}", request.transactionId(), throwable.getMessage());

        if (throwable instanceof FeignException.BadRequest
                || throwable instanceof FeignException.NotFound) {
            throw new InvalidDepositException(throwable.getMessage());
        }

        throw new WalletClientUnavailableException("Erro ao acessar serviço de carteira: " + throwable.getMessage());
    }

}
