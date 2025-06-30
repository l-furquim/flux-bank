package com.fluxbank.wallet_service.domain.service;

import com.fluxbank.wallet_service.application.dto.CreateWalletDto;
import com.fluxbank.wallet_service.domain.enums.WalletStatus;
import com.fluxbank.wallet_service.domain.exception.DuplicatedWalletCurrencyException;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.infrastructure.persistence.adapter.WalletPersistenceAdapter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WalletDomainService {

    private final WalletPersistenceAdapter adapter;

    public WalletDomainService(WalletPersistenceAdapter adapter) {
        this.adapter = adapter;
    }

    public Wallet createWallet(CreateWalletDto dto){

        boolean alreadyHaveCurrentCurrencyWallet = adapter.findWalletsByUserId(dto.userId())
                .stream()
                .anyMatch(w -> w.getCurrency().equals(dto.currency()));

        if(alreadyHaveCurrentCurrencyWallet) {
            throw new DuplicatedWalletCurrencyException("Cannot have two wallets with the same currency");
        }

        Wallet wallet =  Wallet.builder()
                .createdAt(LocalDateTime.now())
                .walletStatus(WalletStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .blockedAmount(BigDecimal.ZERO)
                .userId(dto.userId())
                .currency(dto.currency())
                .build();

        adapter.saveWallet(wallet);

        return wallet;
    }

}
