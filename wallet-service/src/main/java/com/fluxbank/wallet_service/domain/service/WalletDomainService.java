package com.fluxbank.wallet_service.domain.service;

import com.fluxbank.wallet_service.application.dto.*;
import com.fluxbank.wallet_service.application.port.WalletLimitPort;
import com.fluxbank.wallet_service.application.port.WalletPort;
import com.fluxbank.wallet_service.application.port.WalletTransactionPort;
import com.fluxbank.wallet_service.domain.enums.Currency;
import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.domain.enums.TransactionType;
import com.fluxbank.wallet_service.domain.enums.WalletStatus;
import com.fluxbank.wallet_service.domain.exception.wallet.DuplicatedWalletCurrencyException;
import com.fluxbank.wallet_service.domain.exception.wallet.UnauthorizedWithDrawRequest;
import com.fluxbank.wallet_service.domain.exception.wallet.UnnauthorizedBalanceRequestException;
import com.fluxbank.wallet_service.domain.exception.wallet.WalletNotFoundException;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;
import com.fluxbank.wallet_service.infrastructure.persistence.adapter.WalletPersistenceAdapter;
import com.fluxbank.wallet_service.infrastructure.service.WalletEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class WalletDomainService implements WalletPort {

    private final WalletPersistenceAdapter adapter;
    private final WalletTransactionPort walletTransactionService;
    private final WalletEventService eventService;
    private final WalletLimitPort walletLimitService;

    public WalletDomainService(WalletPersistenceAdapter adapter, WalletTransactionPort walletTransactionService, WalletEventService eventService, WalletLimitPort walletLimitService) {
        this.adapter = adapter;
        this.walletTransactionService = walletTransactionService;
        this.eventService = eventService;
        this.walletLimitService = walletLimitService;
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

        walletLimitService.createInitialLimit(wallet);

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

        UUID transactionId = UUID.fromString(data.transactionId());

        WalletTransaction walletTransaction = walletTransactionService.create(new CreateWalletTransactionDto(
                wallet,
                transactionId,
                data.type(),
                data.amount(),
                data.description(),
                data.metadata(),
                Optional.of(TransactionStatus.COMPLETED)
        ));

        wallet.deposit(data.amount());

        adapter.updateWalletBalance(wallet.getBalance(), wallet.getId());

        long processedMs = Duration.between(
                walletTransaction.getCreatedAt(),
                LocalDateTime.now()
        ).toMillis();

        eventService.sendTransactionConfirmation(
                new WalletUpdatedEventDto(
                        walletTransaction.getTransactionId(),
                        wallet.getUserId().toString(),
                        walletTransaction.getTransactionType().toString(),
                        walletTransaction.getStatus().toString(),
                        data.amount(),
                        wallet.getCurrency().toString(),
                        processedMs,
                        Instant.now(),
                        "walletDomainService"
                )
        );

        return new TransactionResult(
                transactionId,
                data.type(),
                wallet.getCurrency(),
                data.amount(),
                walletTransaction.getCreatedAt()
        );
    }

    // @Cacheable(value = "wallet-deposit", key = "#request.walletId()" + "_" + "#userId")
    @Override
    public GetWalletBalanceResponse balance(GetWalletBalanceRequest request, UUID userId) {
        UUID walletId = UUID.fromString(request.walletId());

        Wallet wallet = adapter.findWalletById(walletId);

        if (wallet == null) {
            throw new WalletNotFoundException();
        }

        if (!wallet.getUserId().equals(userId)) {
            throw new UnnauthorizedBalanceRequestException();
        }

        return new GetWalletBalanceResponse(
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.getBlockedAmount(),
                wallet.getWalletStatus()
        );
    }

    @Override
    public WithDrawResponse withDraw(WithDrawRequest request, String userId) {
        UUID walletId = UUID.fromString(request.walletId());

        Wallet wallet = this.adapter.findWalletById(walletId);

        if(wallet == null) {
            throw new WalletNotFoundException();
        }

        UUID userIdConverted = UUID.fromString(userId);

        if(!wallet.getUserId().equals(userIdConverted)){
            throw new UnauthorizedWithDrawRequest();
        }

        WalletTransaction walletTransaction = walletTransactionService.create(new CreateWalletTransactionDto(
                wallet,
                request.transactionId(),
                request.type(),
                request.amount(),
                "",
                request.metadata(),
                Optional.of(TransactionStatus.COMPLETED)
        ));

        if(request.type().equals(TransactionType.CREDIT))  {
            walletLimitService.updateWalletLimit();
        }

        wallet.withDraw(request.amount());

        adapter.updateWalletBalance(wallet.getBalance(), wallet.getId());


    }

}
