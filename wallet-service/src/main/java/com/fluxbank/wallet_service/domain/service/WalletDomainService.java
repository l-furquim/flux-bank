package com.fluxbank.wallet_service.domain.service;

import com.fluxbank.wallet_service.application.dto.CreateWalletRequest;
import com.fluxbank.wallet_service.application.dto.CreateWalletTransactionDto;
import com.fluxbank.wallet_service.application.dto.DepositInWalletRequest;
import com.fluxbank.wallet_service.application.dto.TransactionResult;
import com.fluxbank.wallet_service.application.port.WalletPort;
import com.fluxbank.wallet_service.application.port.WalletTransactionPort;
import com.fluxbank.wallet_service.domain.enums.Currency;
import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.domain.enums.WalletStatus;
import com.fluxbank.wallet_service.domain.exception.wallet.DuplicatedWalletCurrencyException;
import com.fluxbank.wallet_service.domain.exception.wallet.WalletNotFoundException;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;
import com.fluxbank.wallet_service.infrastructure.persistence.adapter.WalletPersistenceAdapter;
import com.fluxbank.wallet_service.infrastructure.service.TokenService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletDomainService implements WalletPort {

    private final WalletPersistenceAdapter adapter;
    private final WalletTransactionPort walletTransactionService;

    public WalletDomainService(WalletPersistenceAdapter adapter, WalletTransactionPort walletTransactionService, TokenService tokenService) {
        this.adapter = adapter;
        this.walletTransactionService = walletTransactionService;
    }

    public Wallet createWallet(CreateWalletRequest dto, String suserId){
        UUID userId = UUID.fromString(suserId);

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

        WalletTransaction transaction = walletTransactionService.create(new CreateWalletTransactionDto(
                wallet,
                data.type(),
                data.amount(),
                data.description(),
                data.metadata(),
                Optional.of(TransactionStatus.COMPLETED)
        ));

        if(wallet == null) {
            throw new WalletNotFoundException();
        }

        wallet.deposit(data.amount());

        adapter.saveWallet(wallet);

        return new TransactionResult(
                transaction.getId(),
                wallet.getCurrency(),
                data.amount(),
                transaction.getCreatedAt()
        );
    }

}
