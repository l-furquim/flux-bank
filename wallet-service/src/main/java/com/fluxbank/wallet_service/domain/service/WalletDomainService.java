package com.fluxbank.wallet_service.domain.service;

import com.fluxbank.wallet_service.application.dto.*;
import com.fluxbank.wallet_service.application.port.WalletLimitPort;
import com.fluxbank.wallet_service.application.port.WalletPort;
import com.fluxbank.wallet_service.application.port.WalletTransactionPort;
import com.fluxbank.wallet_service.domain.enums.Currency;
import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.domain.enums.WalletStatus;
import com.fluxbank.wallet_service.domain.exception.wallet.*;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletLimit;
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

        Wallet walletPersisted = adapter.saveWallet(wallet);

        log.info("ID da wallet criada: {}", walletPersisted.getId());

        walletLimitService.createInitialLimit(walletPersisted);

        return walletPersisted;
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
    public TransactionResult withDraw(WithDrawRequest request, String userId) {
        UUID walletId = UUID.fromString(request.walletId());

        Wallet wallet = this.adapter.findWalletById(walletId);

        if(wallet == null) {
            throw new WalletNotFoundException();
        }

        UUID userIdConverted = UUID.fromString(userId);

        if(!wallet.getUserId().equals(userIdConverted)){
            throw new UnauthorizedWithDrawRequest();
        }

        if(!wallet.hasAvailableBalance(request.amount())){
            throw new InsufficientBalanceException();
        }

        WalletTransaction transaction = walletTransactionService.create(new CreateWalletTransactionDto(
                wallet,
                request.transactionId(),
                request.type(),
                request.amount(),
                "",
                request.metadata(),
                Optional.of(TransactionStatus.COMPLETED)
        ));

        walletLimitService.updateWalletLimit(new UpdateWalletLimitRequest(wallet, request.amount(), request.type()));

        wallet.withDraw(request.amount());

        adapter.updateWalletBalance(wallet.getBalance(), wallet.getId());

        long processedMs = Duration.between(
                transaction.getCreatedAt(),
                LocalDateTime.now()
        ).toMillis();

        eventService.sendTransactionConfirmation(
                new WalletUpdatedEventDto(
                        transaction.getTransactionId(),
                        wallet.getUserId().toString(),
                        transaction.getTransactionType().toString(),
                        transaction.getStatus().toString(),
                        request.amount(),
                        wallet.getCurrency().toString(),
                        processedMs,
                        Instant.now(),
                        "walletDomainService"
                )
        );

        return new TransactionResult(
                request.transactionId(),
                request.type(),
                wallet.getCurrency(),
                request.amount(),
                transaction.getCreatedAt()
        );
    }

    @Override
    public GetWalletLimitsResponse getLimits(GetWalletLimitsRequest request, UUID userId) {
        UUID walletId = UUID.fromString(request.walletId());

        Wallet wallet = this.adapter.findWalletById(walletId);

        if(wallet == null) {
            throw new WalletNotFoundException();
        }

        validateWalletUsage(wallet, userId);

        List<WalletLimit> limits = this.walletLimitService.findByWallet(wallet);

        List<LimitInformationDto> infos = limits
                .stream()
                .map(limit -> {
                    return new LimitInformationDto(
                            limit.getLimitType(),
                            limit.getLimitAmount(),
                            limit.getUsedAmount(),
                            limit.getStatus(),
                            limit.getUpdatedAt()
                    );
                }).toList();

        return new GetWalletLimitsResponse(infos);
    }

    private void validateWalletUsage(Wallet wallet, UUID userId){
        if(!wallet.getUserId().equals(userId)) {
            throw new UnauthorizedOperationException("Unauthorized operation.");
        }

        if(wallet.isClosed()){
            throw new UnauthorizedOperationException("The wallet is current closed.");
        }

        if(!wallet.isAllowedToUse()){
            throw new UnauthorizedOperationException("This wallet is blocked or suspend, please verify the wallet status and see what you can do.");
        }
    }

}
