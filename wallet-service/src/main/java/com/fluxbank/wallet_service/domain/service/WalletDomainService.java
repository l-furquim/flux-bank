package com.fluxbank.wallet_service.domain.service;

import com.fluxbank.wallet_service.application.dto.*;
import com.fluxbank.wallet_service.application.port.WalletLimitPort;
import com.fluxbank.wallet_service.application.port.WalletPort;
import com.fluxbank.wallet_service.application.port.WalletTransactionPort;
import com.fluxbank.wallet_service.domain.enums.Currency;
import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.domain.enums.WalletStatus;
import com.fluxbank.wallet_service.domain.exception.wallet.*;
import com.fluxbank.wallet_service.domain.exception.wallettransaction.InvalidWalletRefundException;
import com.fluxbank.wallet_service.domain.exception.wallettransaction.WalletTransactionNotFoundException;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletLimit;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;
import com.fluxbank.wallet_service.infrastructure.persistence.adapter.WalletPersistenceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    private final WalletLimitPort walletLimitService;

    public WalletDomainService(WalletPersistenceAdapter adapter, WalletTransactionPort walletTransactionService, WalletLimitPort walletLimitService) {
        this.adapter = adapter;
        this.walletTransactionService = walletTransactionService;
        this.walletLimitService = walletLimitService;
    }

    @CachePut(value = "wallets", key = "#result.id")
    @CacheEvict(value = "user-wallets", key = "#userId")
    @Override
    public Wallet createWallet(CreateWalletRequest dto, UUID userId){
        Currency currencyConverted = Currency.fromValue(dto.currency());

        List<Wallet> userWallets = findWalletsByUserId(userId);

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

    @Caching(evict = {
            @CacheEvict(value = "wallets", key = "#result.transactionId"),
            @CacheEvict(value = "wallet-balance", key = "#data.userId + '_' + #data.currency().name()"),
            @CacheEvict(value = "wallet-limits", key = "#data.userId + '_' + #data.currency().name()"),
            @CacheEvict(value = "user-wallets", key = "#data.userId")
    })
    @Override
    public TransactionResult deposit(DepositInWalletRequest data){
        UUID userId = UUID.fromString(data.userId());

        Wallet wallet = this.findWalletsByUserId(userId)
                .stream()
                .filter(w -> w.getCurrency().equals(data.currency()))
                .findFirst()
                .orElseThrow(WalletNotFoundException::new);

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

        updateWalletCache(wallet);

        return new TransactionResult(
                transactionId,
                data.type(),
                wallet.getCurrency(),
                data.amount(),
                walletTransaction.getCreatedAt()
        );
    }

    @Cacheable(
            value = "wallet-balance",
            key = "#request.walletId() + '_' + #userId.toString()",
            unless = "#result == null"
    )
    @Override
    public GetWalletBalanceResponse balance(GetWalletBalanceRequest request, UUID userId) {
        UUID walletId = UUID.fromString(request.walletId());

        Wallet wallet = this.findWalletById(walletId);

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

    @Caching(evict = {
            @CacheEvict(value = "wallets", key = "#result.transactionId"),
            @CacheEvict(value = "wallet-balance", key = "#request.userId + '_' + #request.currency().name()"),
            @CacheEvict(value = "wallet-limits", key = "#request.userId + '_' + #request.currency().name()"),
            @CacheEvict(value = "user-wallets", key = "#request.userId")
    })
    @Override
    public TransactionResult withDraw(WithDrawRequest request, String userId) {
        UUID userIdFormated = UUID.fromString(request.userId());

        Wallet wallet = this.findWalletsByUserId(userIdFormated)
                .stream()
                .filter(w -> w.getCurrency().equals(request.currency()))
                .findFirst()
                .orElseThrow(WalletNotFoundException::new);

        UUID userIdConverted = UUID.fromString(userId);

        if(!wallet.getUserId().equals(userIdConverted)){
            throw new UnauthorizedWithDrawRequest();
        }

        if(!wallet.hasAvailableBalance(request.amount())){
            throw new InsufficientBalanceException();
        }

        walletLimitService.updateWalletLimit(new UpdateWalletLimitRequest(wallet, request.amount(), request.type()));

        WalletTransaction transaction = walletTransactionService.create(new CreateWalletTransactionDto(
                wallet,
                request.transactionId(),
                request.type(),
                request.amount(),
                "",
                request.metadata(),
                Optional.of(TransactionStatus.COMPLETED)
        ));

        wallet.withDraw(request.amount());

        adapter.updateWalletBalance(wallet.getBalance(), wallet.getId());

        updateWalletCache(wallet);

        return new TransactionResult(
                request.transactionId(),
                request.type(),
                wallet.getCurrency(),
                request.amount(),
                transaction.getCreatedAt()
        );
    }

    @Cacheable(
            value = "wallet-limits",
            key = "#request.walletId() + '_' + #userId.toString()",
            unless = "#result == null"
    )
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

    @Caching(evict = {
            @CacheEvict(value = "wallets", allEntries = true),
            @CacheEvict(value = "wallet-balance", allEntries = true),
            @CacheEvict(value = "wallet-limits", allEntries = true),
            @CacheEvict(value = "user-wallets", allEntries = true)
    })
    @Override
    public void refund(RefundWalletTransactionRequest request) {
        UUID walletTransactionId = null;
        UUID payeeId = null;

        try {
            walletTransactionId = UUID.fromString(request.walletTransactionId());
            payeeId = UUID.fromString(request.payeeId());
        } catch (IllegalArgumentException e) {
            throw new InvalidWalletRefundException("Invalid data for refunding the wallet.");
        }

        WalletTransaction transactionToBeRefund = this.walletTransactionService.findById(walletTransactionId);

        if(transactionToBeRefund == null) {
            throw new WalletTransactionNotFoundException("Could not found the transaction.");
        }

        Wallet payerWallet = this.adapter.findWalletById(transactionToBeRefund.getWallet().getId());

        Wallet payeeWallet = this.findWalletsByUserId(payeeId)
                        .stream()
                        .filter(w -> w.getCurrency().equals(transactionToBeRefund.getWallet().getCurrency()))
                        .findFirst()
                        .orElseThrow(WalletNotFoundException::new);

        payeeWallet.withDraw(transactionToBeRefund.getAmount());
        payerWallet.deposit(transactionToBeRefund.getAmount());

        walletLimitService.rollbackWalletLimit(
                payerWallet,
                transactionToBeRefund
        );

        walletTransactionService.createRefundTransaction(
                transactionToBeRefund,
                payeeWallet
        );

        adapter.updateWallet(payerWallet);
        adapter.updateWallet(payeeWallet);

        updateWalletCache(payerWallet);
        updateWalletCache(payeeWallet);

        log.info("Wallet: {} was refunded by the transaction: {}", payerWallet.getId(), transactionToBeRefund.getId());
    }

    @Cacheable(value = "wallets", key = "#walletId", unless = "#result == null")
    public Wallet findWalletById(UUID walletId) {
        return adapter.findWalletById(walletId);
    }

    @Cacheable(value = "user-wallets", key = "#userId", unless = "#result == null or #result.isEmpty()")
    public List<Wallet> findWalletsByUserId(UUID userId) {
        return this.adapter.findWalletsByUserId(userId);
    }

    @CachePut(value = "wallets", key = "#wallet.id")
    private Wallet updateWalletCache(Wallet wallet) {
        return wallet;
    }

    @CacheEvict(value = {"wallets", "wallet-balance", "wallet-limits"}, key = "#walletId")
    public void evictWalletCache(UUID walletId) {
        log.debug("Cache invalidated for wallet: {}", walletId);
    }

    @Caching(evict = {
            @CacheEvict(value = "user-wallets", key = "#userId"),
            @CacheEvict(value = "wallet-balance", allEntries = true),
            @CacheEvict(value = "wallet-limits", allEntries = true)
    })
    public void evictUserWalletsCache(UUID userId) {
        log.debug("All wallet caches invalidated for user: {}", userId);
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
