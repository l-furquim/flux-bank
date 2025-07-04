package com.fluxbank.wallet_service.domain.service;

import com.fluxbank.wallet_service.application.dto.*;
import com.fluxbank.wallet_service.application.port.WalletPort;
import com.fluxbank.wallet_service.application.port.WalletTransactionPort;
import com.fluxbank.wallet_service.domain.enums.Currency;
import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.domain.enums.WalletStatus;
import com.fluxbank.wallet_service.domain.exception.wallet.DuplicatedWalletCurrencyException;
import com.fluxbank.wallet_service.domain.exception.wallet.UnnauthorizedBalanceRequestException;
import com.fluxbank.wallet_service.domain.exception.wallet.WalletNotFoundException;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;
import com.fluxbank.wallet_service.infrastructure.persistence.adapter.WalletPersistenceAdapter;
import com.fluxbank.wallet_service.infrastructure.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class WalletDomainService implements WalletPort {

    private final WalletPersistenceAdapter adapter;
    private final WalletTransactionPort walletTransactionService;

    public WalletDomainService(WalletPersistenceAdapter adapter, WalletTransactionPort walletTransactionService, TokenService tokenService) {
        this.adapter = adapter;
        this.walletTransactionService = walletTransactionService;
    }

    // @CachePut(value = "wallets", key = "#result.id")
    @Override
    public Wallet createWallet(CreateWalletRequest dto, UUID userId){
        Currency currencyConverted = Currency.fromValue(dto.currency());

        List<Wallet> userWallets = adapter.findWalletsByUserId(userId);

        boolean alreadyHaveCurrentCurrencyWallet = userWallets
                .stream()
                .anyMatch(w -> w.getCurrency().equals(currencyConverted));

        if(alreadyHaveCurrentCurrencyWallet) {
            throw new DuplicatedWalletCurrencyException("Cannot have two wallets with the same currency");
        }

        Wallet wallet = Wallet.builder()
                .walletStatus(WalletStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .blockedAmount(BigDecimal.ZERO)
                .userId(userId)
                .currency(currencyConverted)
                .build();

        return adapter.saveWallet(wallet);
    }

    // @CacheEvict()
    @Override
    public TransactionResult deposit(DepositInWalletRequest data){
        UUID walletId = UUID.fromString(data.walletId());

        Wallet wallet = adapter.findWalletById(walletId);

        if(wallet == null) {
            throw new WalletNotFoundException();
        }

        WalletTransaction transaction = walletTransactionService.create(new CreateWalletTransactionDto(
                wallet,
                data.type(),
                data.amount(),
                data.description(),
                data.metadata(),
                Optional.of(TransactionStatus.COMPLETED)
        ));

        wallet.deposit(data.amount());

        adapter.updateWalletBalance(wallet.getBalance(), wallet.getId());

        return new TransactionResult(
                transaction.getId(),
                wallet.getCurrency(),
                data.amount(),
                transaction.getCreatedAt()
        );
    }

    // @Cacheable(value = "wallet-deposit", key = "#request.walletId()" + "_" + "#userId")
    @Override
    public GetWalletBalanceResponse balance(GetWalletBalanceRequest request, UUID userId) {
        UUID walletId = UUID.fromString(request.walletId());

        Wallet wallet = adapter.findWalletById(walletId);

        if(wallet == null) {
            throw new WalletNotFoundException();
        }

        if(!wallet.getUserId().equals(userId)) {
            throw new UnnauthorizedBalanceRequestException();
        }

        return new GetWalletBalanceResponse(
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.getBlockedAmount(),
                wallet.getWalletStatus()
        );
    }

}
