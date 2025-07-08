package com.fluxbank.wallet_service.domain.service;

import com.fluxbank.wallet_service.application.dto.UpdateWalletLimitRequest;
import com.fluxbank.wallet_service.application.port.WalletLimitPort;
import com.fluxbank.wallet_service.domain.enums.LimitStatus;
import com.fluxbank.wallet_service.domain.enums.LimitType;
import com.fluxbank.wallet_service.domain.exception.walletlimit.LimitBlockedException;
import com.fluxbank.wallet_service.domain.exception.walletlimit.UnavailableLimitException;
import com.fluxbank.wallet_service.domain.exception.walletlimit.WalletLimitNotFoundException;
import com.fluxbank.wallet_service.domain.models.Wallet;
import com.fluxbank.wallet_service.domain.models.WalletLimit;
import com.fluxbank.wallet_service.infrastructure.persistence.adapter.WalletLimitAdapter;
import com.fluxbank.wallet_service.infrastructure.persistence.entity.WalletEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletLimitDomainService implements WalletLimitPort {

    private final WalletLimitAdapter adapter;

    public WalletLimitDomainService(WalletLimitAdapter adapter) {
        this.adapter = adapter;
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
        Optional<WalletLimit> limitFounded = adapter.findWalletLimitByTypeAndWalletId(request.type(), request.wallet());

        if(limitFounded.isEmpty()) {
            throw new WalletLimitNotFoundException();
        }

        WalletLimit limit = limitFounded.get();

        if(
                limit.getStatus().equals(LimitStatus.EXCEEDED) |
                limit.getStatus().equals(LimitStatus.INACTIVE) |
                limit.isLimitExceeded()
        ) {
            throw new LimitBlockedException();
        }

        if(!limit.hasAvailableLimit(request.amount())){
            throw new UnavailableLimitException("You dont have limit to do that operation, please wait til the reset date");
        }

        

    }

    @Override
    public void resetWalletLimit(UUID walletLimitId) {

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
}
