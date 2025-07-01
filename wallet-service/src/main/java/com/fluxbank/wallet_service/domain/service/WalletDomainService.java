package com.fluxbank.wallet_service.domain.service;

import com.fluxbank.wallet_service.application.dto.CreateWalletRequest;
import com.fluxbank.wallet_service.application.dto.DepositInWalletRequest;
import com.fluxbank.wallet_service.application.dto.TransactionResult;
import com.fluxbank.wallet_service.application.port.WalletPort;
import com.fluxbank.wallet_service.domain.enums.Currency;
import com.fluxbank.wallet_service.domain.enums.WalletStatus;
import com.fluxbank.wallet_service.domain.exception.wallet.DuplicatedWalletCurrencyException;
import com.fluxbank.wallet_service.domain.exception.wallet.InvalidDepositException;
import com.fluxbank.wallet_service.domain.exception.wallet.WalletNotFoundException;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.infrastructure.persistence.adapter.WalletPersistenceAdapter;
import com.fluxbank.wallet_service.infrastructure.service.TokenService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WalletDomainService implements WalletPort {

    private final WalletPersistenceAdapter adapter;
    private final TokenService tokenService;

    public WalletDomainService(WalletPersistenceAdapter adapter, TokenService tokenService) {
        this.adapter = adapter;
        this.tokenService = tokenService;
    }

    public Wallet createWallet(CreateWalletRequest dto, String token){
        UUID userId = UUID.fromString(tokenService.getTokenData(token).getUserId());

        Currency currencyConverted = Currency.fromValue(dto.currency());

        boolean alreadyHaveCurrentCurrencyWallet = adapter.findWalletsByUserId(userId)
                .stream()
                .anyMatch(w -> w.getCurrency().equals(currencyConverted));

        if(alreadyHaveCurrentCurrencyWallet) {
            throw new DuplicatedWalletCurrencyException("Cannot have two wallets with the same currency");
        }

        Wallet wallet =  Wallet.builder()
                .createdAt(LocalDateTime.now())
                .walletStatus(WalletStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .blockedAmount(BigDecimal.ZERO)
                .userId(userId)
                .currency(currencyConverted)
                .build();

        adapter.saveWallet(wallet);

        return wallet;
    }

    public TransactionResult deposit(DepositInWalletRequest data){
        Wallet wallet = adapter.findWalletById(data.walletId());

        if(wallet == null) {
            throw new WalletNotFoundException();
        }

        if(wallet.getWalletStatus().equals(WalletStatus.BLOCKED) | wallet.getWalletStatus().equals(WalletStatus.CLOSED)) {
            throw new InvalidDepositException();
        }

        if(data.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDepositException();
        }



    }

}
