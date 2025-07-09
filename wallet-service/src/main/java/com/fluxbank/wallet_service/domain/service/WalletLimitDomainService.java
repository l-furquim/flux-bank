package com.fluxbank.wallet_service.domain.service;

import com.fluxbank.wallet_service.application.dto.UpdateWalletLimitRequest;
import com.fluxbank.wallet_service.application.port.WalletLimitPort;
import com.fluxbank.wallet_service.domain.enums.LimitStatus;
import com.fluxbank.wallet_service.domain.enums.LimitType;
import com.fluxbank.wallet_service.domain.enums.TransactionType;
import com.fluxbank.wallet_service.domain.exception.walletlimit.LimitBlockedException;
import com.fluxbank.wallet_service.domain.exception.walletlimit.UnavailableLimitException;
import com.fluxbank.wallet_service.domain.exception.walletlimit.WalletLimitNotFoundException;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletLimit;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;
import com.fluxbank.wallet_service.domain.strategy.WalletLimitResetStrategy;
import com.fluxbank.wallet_service.domain.strategy.factories.WalletLimitResetStrategyFactory;
import com.fluxbank.wallet_service.infrastructure.persistence.adapter.WalletLimitAdapter;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class WalletLimitDomainService implements WalletLimitPort {

    private final WalletLimitAdapter adapter;
    private final WalletTransactionDomainService transactionService;
    private final WalletLimitResetStrategyFactory strategyFactory;

    public WalletLimitDomainService(WalletLimitAdapter adapter, WalletTransactionDomainService transactionService, WalletLimitResetStrategyFactory strategyFactory) {
        this.adapter = adapter;
        this.transactionService = transactionService;
        this.strategyFactory = strategyFactory;
    }

    @Override
    public void createInitialLimit(Wallet wallet) {
        LocalDateTime now = LocalDateTime.now();

        Arrays.stream(LimitType.values())
                .forEach(type -> {
                    WalletEntity walletEntity = new WalletEntity();

                    walletEntity.setId(wallet.getId());

                    WalletLimit limit = WalletLimit.builder()
                            .wallet(wallet)
                            .limitType(type)
                            .limitAmount(defaultLimitAmount(type))
                            .resetDate(calculateResetDate(type))
                            .build();
                    adapter.create(limit, walletEntity);
                });
    }

    @Override
    public void updateWalletLimit(UpdateWalletLimitRequest request) {
        List<WalletLimit> limits = adapter.findByWalletId(request.wallet().getId());

        if (limits.isEmpty()) {
            throw new WalletLimitNotFoundException();
        }

        List<WalletLimit> applicableLimits = limits.stream()
                .filter(limit -> isApplicable(limit, request.type()))
                .toList();

        for (WalletLimit limit : applicableLimits) {
            if (limit.getStatus() == LimitStatus.EXCEEDED ||
                    limit.getStatus() == LimitStatus.INACTIVE ||
                    limit.isLimitExceeded()) {
                throw new LimitBlockedException();
            }

            if (!limit.hasAvailableLimit(request.amount())) {
                throw new UnavailableLimitException(
                        "You don't have %s limit to do that operation, please wait until the reset date."
                                .formatted(limit.getLimitType().toString().toLowerCase())
                );
            }

            limit.subtractLimit(request.amount());

            if (limit.isLimitExceeded()) {
                limit.setStatus(LimitStatus.EXCEEDED);
            }

            adapter.updateWalletLimit(limit.getId(), limit.getLimitAmount(), limit.getStatus());
        }
    }

    @Override
    public void resetWalletLimit(UUID walletLimitId) {
        WalletLimit limit = adapter.findById(walletLimitId)
                .orElseThrow(WalletLimitNotFoundException::new);

        if (limit.getStatus().equals(LimitStatus.INACTIVE)) return;

        WalletLimitResetStrategy strategy = strategyFactory.getStrategy(limit.getLimitType());

        if (strategy == null) return;

        int days = strategy.getUsageWindowDays();
        LocalDateTime since = LocalDateTime.now().minusDays(days);

        List<TransactionType> types = getCorrectType(limit.getLimitType());

        List<WalletTransaction> transactions = transactionService
                .getWalletTransactionsByTypesAndWallet(limit.getWallet().getId(), types, since);

        BigDecimal newLimit = strategy.calculateNewLimit(limit, transactions);

        limit.setLimitAmount(newLimit);
        limit.setStatus(LimitStatus.ACTIVE);

        adapter.updateWalletLimit(limit.getId(), limit.getLimitAmount(), limit.getStatus());
    }

    private List<TransactionType> getCorrectType(LimitType type) {
        return switch (type) {
            case DAILY_PIX, MONTHLY_PIX -> List.of(TransactionType.PIX);
            case DAILY_TRANSACTION, MONTHLY_TRANSACTION -> List.of(TransactionType.DEBIT, TransactionType.CREDIT, TransactionType.PIX);
            case SINGLE_TRANSACTION -> List.of(TransactionType.DEBIT, TransactionType.PIX);
        };
    }

    private BigDecimal defaultLimitAmount(LimitType type) {
        return switch (type) {
            case DAILY_PIX -> BigDecimal.valueOf(1500);
            case DAILY_TRANSACTION -> BigDecimal.valueOf(3000);
            case SINGLE_TRANSACTION -> BigDecimal.valueOf(1000);
            case MONTHLY_TRANSACTION, MONTHLY_PIX -> BigDecimal.valueOf(10000);
        };
    }

    private LocalDateTime calculateResetDate(LimitType type) {
        LocalDateTime now = LocalDateTime.now();
        return switch (type) {
            case DAILY_PIX, DAILY_TRANSACTION -> now.toLocalDate().plusDays(1).atStartOfDay();
            case MONTHLY_TRANSACTION, MONTHLY_PIX -> now.toLocalDate().plusMonths(1).withDayOfMonth(1).atStartOfDay();
            case SINGLE_TRANSACTION -> null;
        };
    }

    private boolean isApplicable(WalletLimit limit, TransactionType transactionType) {
        return switch (limit.getLimitType()) {
            case DAILY_PIX, MONTHLY_PIX -> transactionType == TransactionType.PIX;
            case DAILY_TRANSACTION, MONTHLY_TRANSACTION, SINGLE_TRANSACTION -> true;
        };
    }
}
